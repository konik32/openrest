package pl.openrest.dto.handler.spel;

import java.lang.reflect.Field;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelEvaluator {
    private final SpelExpressionParser parser;
    private final ParserContext parserContext;
    private final EvaluationContext evaluationContext;

    public SpelEvaluator(DtoEvaluationWrapper wrapper, BeanFactory beanFactory) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(wrapper);
        evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        this.evaluationContext = evaluationContext;
        this.parser = new SpelExpressionParser();
        this.parserContext = new TemplateParserContext();
    }

    public Object evaluate(Field field) {
        Value annotation = field.getAnnotation(Value.class);
        return evaluate(annotation);
    }

    private Object evaluate(Value annotation) {
        if (annotation == null) {
            return null;
        }
        Expression expression = parser.parseExpression(annotation.value(), parserContext);
        return expression.getValue(evaluationContext);
    }

    public Boolean evaluateAsBoolean(String exp) {
        Expression expression = parser.parseExpression(exp, parserContext);
        return ((Boolean) expression.getValue(evaluationContext, Boolean.class)).booleanValue();
    }

}
