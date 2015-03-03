package orest.expression.registry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import orest.repository.PredicateContextQueryDslRepository;
import orest.repository.QueryDslPredicateInvoker;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

public class EntityExpressionMethodsRegistry {

	private Map<Class<?>, ExpressionEntityInformation> registry = new HashMap<Class<?>, ExpressionEntityInformation>();
	private Repositories repositories;
	private PersistentEntities persistentEntities;

	public ExpressionEntityInformation getEntityInformation(Class<?> entityType) {
		return registry.get(entityType);
	}

	public EntityExpressionMethodsRegistry(ListableBeanFactory beanFactory,
			Repositories repositories,PersistentEntities persistentEntities) {
		Assert.notNull(beanFactory);
		Assert.notNull(repositories);
		Assert.notNull(persistentEntities);
		this.repositories = repositories;
		this.persistentEntities = persistentEntities;

		Map<String, Object> expressionRepositoriesMap = beanFactory
				.getBeansWithAnnotation(ExpressionRepository.class);
		for (Object repository : expressionRepositoriesMap.values()) {
			Class<?> entityType = AnnotationUtils.getAnnotation(
					repository.getClass(), ExpressionRepository.class).value();
			PathBuilder<?> builder = new PathBuilderFactory()
					.create(entityType);
			populateRegistry(entityType, repository, builder);
		}
	}

	private ExpressionMethodRegistry getExpresionMethodsInformation(
			Object repository, PathBuilder<?> builder, Class<?> entityType) {
		Method methods[] = ReflectionUtils.getAllDeclaredMethods(repository
				.getClass());
		ExpressionMethodRegistry methodMappings = new ExpressionMethodRegistry();
		for (Method m : methods) {
			if (BooleanExpression.class.isAssignableFrom(m.getReturnType())) {
				ExpressionMethod ann = AnnotationUtils.findAnnotation(m,
						ExpressionMethod.class);
				ExpressionMethodInformation methodInfo = MethodInformationFactory
						.create(entityType, m, builder);
				if (ann == null || ann.exported())
					methodMappings.add(methodInfo);
				if (methodInfo.getStaticFilter() != null)
					methodMappings.addStaticFilter(methodInfo);
			}
		}
		return methodMappings;
	}

	private void populateRegistry(Class<?> entityType,
			Object expressionRepository, PathBuilder<?> builder) {
		QueryDslPredicateInvoker invoker = getQueryDslPredicateInvoker(
				entityType, builder);
		ExpressionEntityInformation information = new ExpressionEntityInformation(
				entityType, getExpresionMethodsInformation(
						expressionRepository, builder, entityType),
				expressionRepository, invoker, persistentEntities.getPersistentEntity(entityType));
		registry.put(entityType, information);
	}

	private QueryDslPredicateInvoker getQueryDslPredicateInvoker(
			Class<?> entityType, PathBuilder<?> builder) {
		Object repository = repositories.getRepositoryFor(entityType);
		Assert.notNull(repository,
				"You must specify PredicateContextQueryDslRepository for " + entityType);
		if (repository instanceof PredicateContextQueryDslRepository)
			return new QueryDslPredicateInvoker(
					(PredicateContextQueryDslRepository) repository, builder);
		throw new IllegalStateException(
				"You must specify PredicateContextQueryDslRepository for " + entityType);
	}

}
