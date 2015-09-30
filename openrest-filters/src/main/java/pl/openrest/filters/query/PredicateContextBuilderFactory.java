package pl.openrest.filters.query;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.util.ReflectionUtils;

import pl.openrest.exception.OrestException;
import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.exception.ORestFiltersExceptionDictionary;
import pl.openrest.filters.predicate.IdConverter;
import pl.openrest.filters.predicate.MethodParameterConverter;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.registry.JoinInformation;
import pl.openrest.filters.query.registry.StaticFilterInformation;
import pl.openrest.predicate.parser.FilterPart;
import pl.openrest.predicate.parser.FilterPart.FilterPartType;
import pl.openrest.predicate.parser.PredicateParts;

import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

public class PredicateContextBuilderFactory {

    private final MethodParameterConverter predicateParameterConverter;
    private final MethodParameterConverter staticFiltersParameterConverter;
    private final IdConverter idConverter;
    private final @Getter PathBuilderFactory pathBuilderFactory = new PathBuilderFactory();

    public PredicateContextBuilderFactory(@NonNull MethodParameterConverter predicateParameterConverter,
            @NonNull MethodParameterConverter staticFiltersParameterConverter, @NonNull IdConverter idConverter) {
        this.predicateParameterConverter = predicateParameterConverter;
        this.staticFiltersParameterConverter = staticFiltersParameterConverter;
        this.idConverter = idConverter;
    }

    public PredicateContextBuilder create(FilterableEntityInformation entityInfo) {
        return new PredicateContextBuilder(entityInfo);
    }

    public class PredicateContextBuilder {

        private List<JoinInformation> joins = new LinkedList<>();
        private BooleanExpression expression;
        private final FilterableEntityInformation entityInfo;
        @SuppressWarnings("rawtypes")
        private final PathBuilder pathBuilder;

        public PredicateContextBuilder(FilterableEntityInformation entityInfo) {
            this.entityInfo = entityInfo;
            this.pathBuilder = pathBuilderFactory.create(entityInfo.getEntityType());
        }

        public PredicateContextBuilder withFilterTree(FilterPart tree) {
            BooleanExpression treeExpression = processTreeRecursively(tree);
            addBooleanExpression(treeExpression);
            return this;
        }

        @SuppressWarnings("unchecked")
        public PredicateContextBuilder withId(PersistentProperty idProperty, Serializable id) {
            Object convertedId = idConverter.convert(idProperty, id);
            addBooleanExpression(pathBuilder.get(idProperty.getName()).eq(convertedId));
            return this;
        }

        public PredicateContextBuilder withPredicateParts(PredicateParts predicateParts) {
            BooleanExpression expression = (BooleanExpression) getExpression(predicateParts);
            addBooleanExpression(expression);
            return this;
        }

        public PredicateContextBuilder withStaticFilters() {
            List<StaticFilterInformation> staticFilters = entityInfo.getStaticFilters();
            for (StaticFilterInformation staticFilter : staticFilters) {
                Object[] parameters = staticFiltersParameterConverter.convert(staticFilter.getPredicateInformation().getMethod(),
                        staticFilter.getParameters());
                BooleanExpression expression = (BooleanExpression) getExpression(staticFilter.getPredicateInformation(), parameters);
                addBooleanExpression(expression);
            }
            return this;
        }

        private void addBooleanExpression(BooleanExpression expression) {
            this.expression = this.expression == null? expression: this.expression.and(expression);
        }

        private BooleanExpression processTreeRecursively(FilterPart part) {
            if (part.getType() == FilterPartType.LEAF) {
                return (BooleanExpression) getExpression(part.getPredicateParts());
            } else {
                BooleanExpression exp = null;
                for (FilterPart p : part.getParts()) {
                    BooleanExpression pExp = processTreeRecursively(p);
                    exp = exp == null ? pExp : part.getType() == FilterPartType.OR ? exp.or(pExp) : exp.and(pExp);
                }
                return exp;
            }
        }

        @SuppressWarnings("rawtypes")
        private Expression getExpression(PredicateParts predicateParts) {
            PredicateInformation predicateInfo = entityInfo.getPredicateInformation(predicateParts.getPredicateName());
            if (predicateInfo == null)
                throw new OrestException(ORestFiltersExceptionDictionary.NO_SUCH_PREDICATE, "No such predicate"
                        + predicateParts.getParameters());
            Object[] parameters = predicateParameterConverter.convert(predicateInfo.getMethod(), predicateParts.getParameters());
            return getExpression(predicateInfo, parameters);
        }

        @SuppressWarnings("rawtypes")
        private Expression getExpression(PredicateInformation predicateInfo, Object[] parameters) {
            joins.addAll(predicateInfo.getJoins());
            return (Expression) ReflectionUtils.invokeMethod(predicateInfo.getMethod(), entityInfo.getPredicateRepository(), parameters);
        }

        public PredicateContext build() {
            return new PredicateContext(joins, expression);
        }
    }

}
