package pl.openrest.filters.predicate;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.MethodParameter;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelMethodParameterConverter extends AbstractMethodParameterConverter {

    private final SpelExpressionParser parser;
    private final EvaluationContext evaluationContext;

    public SpelMethodParameterConverter(BeanFactory beanFactory) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        this.evaluationContext = evaluationContext;
        this.parser = new SpelExpressionParser();
    }

    @Override
    protected Object doConvert(MethodParameter parameter, String rawParameter) {
        Expression expression = parser.parseExpression(rawParameter);
        return expression.getValue(evaluationContext, parameter.getParameterType());
    }

}