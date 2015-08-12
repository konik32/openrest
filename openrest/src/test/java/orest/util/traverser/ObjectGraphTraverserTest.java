package orest.util.traverser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Data;
import orest.util.traverser.ObjectGraphTraverser;
import orest.util.traverser.TraverserCallback;
import orest.util.traverser.TraverserFieldFilter;

import org.apache.commons.lang3.ClassUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class ObjectGraphTraverserTest {

	@Test
	public void should() throws Exception {
		// given
		Map<String,Boo> boos = Collections.singletonMap("boosa", new Boo("warasza",new Roo("warasza")));
		Foo foo = new Foo("asdf", "id", boos);

		ObjectGraphTraverser tr = new ObjectGraphTraverser(new TraverserCallback() {

			@Override
			public void doWith(Field field, Object owner, String path) {
				System.out.println(path + " " + ReflectionUtils.getField(field, owner));

			}
		}, new TraverserFieldFilter() {

			@Override
			public boolean matches(Field field, Object owner, String path) {
				// TODO Auto-generated method stub
				return true;
			}
		}, new TraverserFieldFilter() {

			@Override
			public boolean matches(Field field, Object owner, String path) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		tr.traverse(foo);
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
		final Roo roo;
	}
	
	@Data
	public static class Roo {
		final String address;
	}
}
