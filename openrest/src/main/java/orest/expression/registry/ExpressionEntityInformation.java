package orest.expression.registry;

import lombok.Data;
import orest.repository.QueryDslPredicateInvoker;

import org.springframework.data.mapping.PersistentEntity;

@Data
public class ExpressionEntityInformation {
	private Class<?> entityType;
	private PersistentEntity<?,?> persistentEntity;
	private ExpressionMethodRegistry methodRegistry;
	private QueryDslPredicateInvoker predicateInvoker;
	private Object expressionRepository;

	public ExpressionEntityInformation(Class<?> entityType,
			ExpressionMethodRegistry mappings, Object expressionRepository, QueryDslPredicateInvoker predicateInvoker,PersistentEntity<?,?> persistentEntity) {
		this.entityType = entityType;
		this.methodRegistry = mappings;
		this.predicateInvoker = predicateInvoker;
		this.expressionRepository = expressionRepository;
		this.persistentEntity = persistentEntity;
	}
	
	public ExpressionMethodInformation getMethodInformation(String name){
		return methodRegistry.get(name);
	}
}
