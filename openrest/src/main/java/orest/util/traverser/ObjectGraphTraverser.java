package orest.util.traverser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Setter;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class ObjectGraphTraverser {

	private static Set<Class<?>> nonTraversableTypes;

	static {
		Set<Class<?>> _nonTraversableTypes = new HashSet<Class<?>>();
		_nonTraversableTypes.add(String.class);
		_nonTraversableTypes.add(Number.class);
		_nonTraversableTypes.add(Class.class);
		_nonTraversableTypes.add(Object.class);
		nonTraversableTypes = Collections.unmodifiableSet(_nonTraversableTypes);
	}

	private final TraverserCallback callback;
	private final TraverserFieldFilter traverseFilter;
	private final TraverserFieldFilter fieldFilter;

	private @Setter TraverserCallback beforeTraverse;
	private @Setter TraverserCallback afterTraverse;

	private List<Traverser> traversers = new ArrayList<Traverser>();

	public ObjectGraphTraverser(TraverserCallback callback, TraverserFieldFilter traverseFilter,
			TraverserFieldFilter fieldFilter, List<Traverser> traversers) {
		this.callback = callback;
		this.traverseFilter = traverseFilter;
		this.fieldFilter = fieldFilter;
		this.traversers.addAll(traversers);
		this.traversers.add(new IterableTraverser());
		this.traversers.add(new MapTraverser());
	}

	public ObjectGraphTraverser(TraverserCallback callback, TraverserFieldFilter traverseFilter,
			TraverserFieldFilter fieldFilter) {
		this(callback, traverseFilter, fieldFilter, (List<Traverser>) Collections.EMPTY_LIST);
	}

	public void traverse(Object root) {
		traverse(root, null);
	}

	public void traverse(final Object target, final String currentPath) {

		ReflectionUtils.doWithFields(target.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtils.makeAccessible(field);
				String path = PathBuilder.appendTo(currentPath, field.getName());
				if (fieldFilter.matches(field, target, path))
					callback.doWith(field, target, path);
				if (traverseFilter.matches(field, target, path)) {
					Object traversableObject = ReflectionUtils.getField(field, target);
					if (traversableObject == null)
						return;
					for (Traverser traverser : traversers) {
						if (traverser.supports(field.getType())) {
							if (beforeTraverse != null)
								beforeTraverse.doWith(field, target, path);
							traverser.traverse(traversableObject, path, ObjectGraphTraverser.this);
							if (afterTraverse != null)
								afterTraverse.doWith(field, target, path);
							return;
						}
					}
					if (isNonTraversable(field.getType())) {
						if (beforeTraverse != null)
							beforeTraverse.doWith(field, target, path);
						traverse(traversableObject, path);
						if (afterTraverse != null)
							afterTraverse.doWith(field, target, path);
					}
				}
			}
		});
	}

	private boolean isNonTraversable(Class<?> clazz) {
		if (clazz.isArray()) {
			return !(ClassUtils.isPrimitiveOrWrapper(clazz.getComponentType()) || nonTraversableTypes.contains(clazz
					.getComponentType()));
		}
		return !(ClassUtils.isPrimitiveOrWrapper(clazz) || nonTraversableTypes.contains(clazz));

	}
}
