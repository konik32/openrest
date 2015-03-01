package openrest.config.query;

import java.util.Iterator;

import lombok.Data;
import openrest.query.filter.StaticFilter;
import openrest.query.filter.StaticFilterFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StaticFilterFactoryUnitTest {

	@Mock
	private PersistentEntities persistentEntities;

	@InjectMocks
	private StaticFilterFactory factory;

	@Before
	public void setUp() {
		Iterator<PersistentEntity<?, ?>> it = mock(Iterator.class);
		PersistentEntity foo = mock(PersistentEntity.class);
		PersistentEntity boo = mock(PersistentEntity.class);
		when(foo.getType()).thenReturn(Foo.class);
		when(foo.findAnnotation(StaticFilter.class)).thenReturn(Foo.class.getAnnotation(StaticFilter.class));
		when(boo.findAnnotation(StaticFilter.class)).thenReturn(Boo.class.getAnnotation(StaticFilter.class));
		when(boo.getType()).thenReturn(Boo.class);
		when(it.hasNext()).thenReturn(true, true, false);
		when(it.next()).thenReturn(foo, boo);
		when(persistentEntities.iterator()).thenReturn(it);
		try {
			factory.afterPropertiesSet();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void doesGetReturnNotEmptyFilterWrappersList() {
		Assert.assertTrue(factory.get(Foo.class, null).size() > 0);
	}

	@Data
	@StaticFilter(name = "fooFilter", value = "like(name,'test');and;eq(price,10.0)")
	private class Foo {
		private String name;
		private Double price;
	}

	@Data
	@StaticFilter(name = "fooFilter", value = "like(username,'test');or;eq(value,10.0);and;eq(foo.price,200.00)")
	private class Boo {
		private String username;
		private Double value;
		private Foo foo;
	}
}
