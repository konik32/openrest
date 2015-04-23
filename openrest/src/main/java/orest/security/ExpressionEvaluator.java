package orest.security;


public interface ExpressionEvaluator {

	Object processParameter(String param, Class<?> type);

	boolean checkCondition(String condition);

}
