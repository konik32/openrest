package pl.openrest.filters.querydsl.query;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

import org.springframework.data.mapping.PersistentProperty;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.predicate.IdConverter;
import pl.openrest.filters.predicate.MethodParameterConverter;
import pl.openrest.filters.predicate.PredicateInformation;
import pl.openrest.filters.predicate.PredicateRepository;
import pl.openrest.filters.query.AbstractPredicateContextBuilderFactory;
import pl.openrest.filters.query.PredicateContextBuilder;
import pl.openrest.filters.query.StaticFilterInformation;
import pl.openrest.filters.querydsl.query.QPredicateContextBuilderFactory.QPredicateContextBuilder;
import pl.openrest.predicate.parser.FilterPart;
import pl.openrest.predicate.parser.FilterPart.FilterPartType;
import pl.openrest.predicate.parser.PredicateParts;

import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

public class QPredicateContextBuilderFactory extends AbstractPredicateContextBuilderFactory<QPredicateContextBuilder> {

    private final @Getter PathBuilderFactory pathBuilderFactory = new PathBuilderFactory();

    public QPredicateContextBuilderFactory(MethodParameterConverter predicateParameterConverter,
            MethodParameterConverter staticFiltersParameterConverter, IdConverter idConverter) {
        super(predicateParameterConverter, staticFiltersParameterConverter, idConverter);
    }

    public QPredicateContextBuilder create(FilterableEntityInformation entityInfo) {
        return new QPredicateContextBuilder(entityInfo);
    }

    public class QPredicateContextBuilder implements PredicateContextBuilder {

        private List<QJoinInformation> joins = new LinkedList<>();
        private BooleanExpression expression;
        private final PredicateRepository predicateRepository;
        @SuppressWarnings("rawtypes")
        private final PathBuilder pathBuilder;

        public QPredicateContextBuilder(FilterableEntityInformation entityInfo) {
            this.predicateRepository = entityInfo.getPredicateRepository();
            this.pathBuilder = pathBuilderFactory.create(entityInfo.getEntityType());
        }

        public PredicateContextBuilder withFilterTree(FilterPart tree) {
            if (tree != null) {
                BooleanExpression treeExpression = processTreeRecursively(tree);
                addBooleanExpression(treeExpression);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        public PredicateContextBuilder withId(PersistentProperty idProperty, Serializable id) {
            Object convertedId = idConverter.convert(idProperty, id);
            addBooleanExpression(pathBuilder.get(idProperty.getName()).eq(convertedId));
            return this;
        }

        public PredicateContextBuilder withPredicateParts(PredicateParts predicateParts) {
            BooleanExpression expression = (BooleanExpression) createExpression(predicateParts);
            addBooleanExpression(expression);
            return this;
        }

        public PredicateContextBuilder withStaticFilters() {
            List<StaticFilterInformation> staticFilters = predicateRepository.getStaticFilters();
            for (StaticFilterInformation staticFilter : staticFilters) {
                if (staticFilterConditionEvaluator == null
                        || !staticFilterConditionEvaluator.evaluateCondition(staticFilter.getCondition())) {
                    Object[] parameters = staticFiltersParameterConverter.convert(staticFilter.getPredicateInformation().getMethod(),
                            staticFilter.getParameters());
                    BooleanExpression expression = (BooleanExpression) createExpression(staticFilter.getPredicateInformation(), parameters);
                    addBooleanExpression(expression);
                }
            }
            return this;
        }

        private void addBooleanExpression(BooleanExpression expression) {
            this.expression = this.expression == null ? expression : this.expression.and(expression);
        }

        private BooleanExpression processTreeRecursively(FilterPart part) {
            if (part.getType() == FilterPartType.LEAF) {
                return (BooleanExpression) createExpression(part.getPredicateParts());
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
        private Expression createExpression(PredicateParts predicateParts) {
            PredicateInformation predicateInfo = predicateRepository.getPredicateInformation(predicateParts.getPredicateName());
            if (predicateInfo == null)
                throw new IllegalArgumentException("No such predicate" + predicateParts.getPredicateName());
            Object[] parameters = predicateParameterConverter.convert(predicateInfo.getMethod(), predicateParts.getParameters());
            return createExpression(predicateInfo, parameters);
        }

        @SuppressWarnings("rawtypes")
        private Expression createExpression(PredicateInformation predicateInfo, Object[] parameters) {
            joins.addAll((List<QJoinInformation>) predicateInfo.getJoins());
            return (Expression) predicateRepository.getPredicate(predicateInfo, parameters);
        }

        public QPredicateContext build() {
            return new QPredicateContext(joins, expression);
        }
    }

}
