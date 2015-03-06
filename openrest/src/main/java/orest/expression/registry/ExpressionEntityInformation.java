package orest.expression.registry;

import lombok.Data;
import orest.repository.QueryDslPredicateInvoker;

import org.springframework.data.mapping.PersistentEntity;

@Data
public class ExpressionEntityInformation {
	private final Class<?> entityType;
	private final PersistentEntity<?,?> persistentEntity;
	private final ExpressionMethodRegistry methodRegistry;
	private final QueryDslPredicateInvoker predicateInvoker;
	private final Object expressionRepository;
	private final boolean defaultedPageable;
	
	public ExpressionMethodInformation getMethodInformation(String name){
		return methodRegistry.get(name);
	}
}
