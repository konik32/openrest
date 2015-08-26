package orest.expression;

import java.util.List;

import lombok.Getter;
import orest.expression.registry.ExpressionEntityInformation;
import orest.expression.registry.ExpressionMethodInformation.Join;
import orest.parser.FilterPart;
import orest.repository.PredicateContext;

import com.mysema.query.types.expr.BooleanExpression;

public class RequestBooleanExpressionBuilder {
    private final ExpressionEntityInformation expEntityInfo;
    private @Getter final PredicateContext predicateContext;
    private final ExpressionBuilder expressionBuilder;
    private @Getter BooleanExpression finalExpression;

    public RequestBooleanExpressionBuilder(ExpressionEntityInformation expEntityInfo, ExpressionBuilder expressionBuilder) {
        this.expEntityInfo = expEntityInfo;
        this.predicateContext = new PredicateContext();
        this.expressionBuilder = expressionBuilder;
    }

    public RequestBooleanExpressionBuilder withSearchMethod(FilterPart searchMethodPart) {
        if(searchMethodPart == null)
            return this;
        BooleanExpression expression = expressionBuilder.createSearchMethodExpression(searchMethodPart.getMethodInfo(), predicateContext,
                expEntityInfo, searchMethodPart.getParameters());
        appendExpression(expression);
        return this;
    }

    public RequestBooleanExpressionBuilder withFilters(FilterPart filtersPartTree) {
        BooleanExpression expression = expressionBuilder.create(filtersPartTree, predicateContext, expEntityInfo.getExpressionRepository());
        appendExpression(expression);
        return this;
    }

    public RequestBooleanExpressionBuilder withStaticFilters() {
        BooleanExpression expression = expressionBuilder.createStaticFiltersExpression(predicateContext, expEntityInfo);
        appendExpression(expression);
        return this;
    }

    public RequestBooleanExpressionBuilder withId(String id) {
        BooleanExpression expression = expressionBuilder.createIdEqualsExpression(id, expEntityInfo);
        appendExpression(expression);
        return this;
    }

    public RequestBooleanExpressionBuilder withExpandJoins(String expand) {
        expressionBuilder.addExpandJoins(predicateContext, expand, expEntityInfo.getEntityType());
        return this;
    }

    public RequestBooleanExpressionBuilder withJoins(List<Join> expands) {
        predicateContext.addJoins(expands);
        return this;
    }

    private void appendExpression(BooleanExpression expression) {
        if (expression == null)
            return;
        finalExpression = finalExpression == null ? expression : finalExpression.and(expression);
    }

}
