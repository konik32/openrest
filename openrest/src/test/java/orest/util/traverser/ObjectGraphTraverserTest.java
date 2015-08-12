package orest.util.traverser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import lombok.Data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ObjectGraphTraverserTest {
	@Mock
	private TraverserCallback callback;
	@Mock
	private TraverserCallback beforeTraverse;
	@Mock
	private TraverserCallback afterTraverse;
	@Mock
	private TraverserFieldFilter fieldFilter;
	private ObjectGraphTraverser traverser;
	private Foo foo;

	@Before
	public void setUp() {
		when(fieldFilter.matches(any(Field.class), any(), anyString())).thenReturn(true);
		Map<String, Boo> boos = Collections.singletonMap("boo", new Boo("Warsaw"));
		foo = new Foo("foo", "id", boos);
		traverser = new ObjectGraphTraverser(callback, fieldFilter, fieldFilter);
	}

	@Test
	public void shouldCallCallback() throws Exception {
		// given

		traverser.setBeforeTraverse(beforeTraverse);
		traverser.setAfterTraverse(afterTraverse);

		// when
		traverser.traverse(foo);
		//
		verify(callback, times(5)).doWith(any(Field.class), any(), anyString());

	}

	@Test
	public void shouldCallBeforeTraverse() throws Exception {
		// given
		traverser.setBeforeTraverse(beforeTraverse);

		// when
		traverser.traverse(foo);
		//
		ArgumentCaptor<Field> argument = ArgumentCaptor.forClass(Field.class);
		verify(beforeTraverse, times(1)).doWith(argument.capture(), any(), anyString());
		assertEquals(Map.class, argument.getValue().getType());
	}

	@Test
	public void shouldCallAfterTraverse() throws Exception {
		// given
		traverser.setAfterTraverse(afterTraverse);
		// when
		traverser.traverse(foo);
		//
		ArgumentCaptor<Field> argument = ArgumentCaptor.forClass(Field.class);
		verify(afterTraverse, times(1)).doWith(argument.capture(), any(), anyString());
		assertEquals(Map.class, argument.getValue().getType());
	}

	@Data
	public static class Foo {
		final String name;
		final boolean valid = true;
		final String id;
		final Map<String, Boo> boos;
	}

	@Data
	public static class Boo {
		final String address;
	}

}
