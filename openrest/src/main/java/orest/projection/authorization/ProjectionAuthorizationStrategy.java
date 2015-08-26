package orest.projection.authorization;

import org.springframework.security.access.AccessDeniedException;

public interface ProjectionAuthorizationStrategy {

    void authorize(String projectionName, Class<?> projectionType) throws AccessDeniedException;
}
