package orest.expression;

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

	public SpelEvaluator(Object target, BeanFactory beanFactory) {
		this(target, beanFactory, true);
	}

	public SpelEvaluator(Object target, BeanFactory beanFactory, boolean wrap) {
		StandardEvaluationContext evaluationContext = new StandardEvaluationContext(wrap ? new TargetWrapper(target)
				: target);
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
		return org.springframework.security.access.expression.ExpressionUtils.evaluateAsBoolean(expression,
				evaluationContext);
	}

	public static class TargetWrapper {

		private final Object target;

		public TargetWrapper(Object target) {
			this.target = target;
		}

		/**
		 * @return the target
		 */
		public Object getTarget() {
			return target;
		}
	}

}