package orest.expression.registry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import orest.expression.registry.ExpressionMethodInformation.MethodType;
import orest.model.User;
import orest.predicates.UserExpressions;
import orest.repository.UserRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;

@RunWith(MockitoJUnitRunner.class)
public class EntityExpressionMethodsRegistryTest {

	@Mock
	private ListableBeanFactory beanFactory;

	@Mock
	private Repositories repositories;

	@Mock
	private PersistentEntities persistentEntities;

	private EntityExpressionMethodsRegistry registry;

	@Before
	public void setUp() {
		when(beanFactory.getBeansWithAnnotation(ExpressionRepository.class)).thenReturn(
				Collections.singletonMap("userExpressions", (Object) new UserExpressions()));
		when(repositories.getRepositoryFor(User.class)).thenReturn(mock(UserRepository.class));
		when(persistentEntities.getPersistentEntity(User.class)).thenReturn(mock(PersistentEntity.class));
	}

	@Test
	public void testIfCreatesRegistry() {
		registry = new EntityExpressionMethodsRegistry(beanFactory, repositories, persistentEntities);
		registry.afterPropertiesSet();
		ExpressionEntityInformation entityInformation = registry.getEntityInformation(User.class);
		Assert.assertNotNull(entityInformation);
		Assert.assertNotNull(entityInformation.getPredicateInvoker());
		ExpressionMethodRegistry methodRegistry = entityInformation.getMethodRegistry();
		Assert.assertNotNull(entityInformation);
		Assert.assertTrue(methodRegistry.getStaticFilters().size()>0);
		Assert.assertEquals(MethodType.SEARCH, methodRegistry.get("nameEq").getMethodType());
		Assert.assertEquals(true, methodRegistry.get("nameEq").isDefaultedPageable());
		Assert.assertEquals(1, methodRegistry.get("nameEq").getMethodParameters().getParameters().size());
		Assert.assertEquals(MethodType.FILTER, methodRegistry.get("surnameEq").getMethodType());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testIfThrowsExceptionOnMissingPredicateContextQueryDslRepository(){
		when(repositories.getRepositoryFor(User.class)).thenReturn(mock(CrudRepository.class));
		registry = new EntityExpressionMethodsRegistry(beanFactory, repositories, persistentEntities);
		registry.afterPropertiesSet();
	}
}
