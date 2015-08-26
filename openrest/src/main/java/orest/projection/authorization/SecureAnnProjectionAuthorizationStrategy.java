package orest.projection.authorization;

import orest.authorization.annotation.Secure;
import orest.security.ExpressionEvaluator;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;

public class SecureAnnProjectionAuthorizationStrategy implements ProjectionAuthorizationStrategy {

    private final ExpressionEvaluator expressionEvaluator;

    public SecureAnnProjectionAuthorizationStrategy(ExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    @Override
    public void authorize(String projectionName, Class<?> projectionType) {
        Secure secure = AnnotationUtils.findAnnotation(projectionType, Secure.class);
        if (secure == null)
            return;
        if (!expressionEvaluator.checkCondition(secure.value()))
            throw new AccessDeniedException("You are not authorized to use projection: " + projectionName);
    }
}
