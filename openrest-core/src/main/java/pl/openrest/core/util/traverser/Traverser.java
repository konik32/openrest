package pl.openrest.core.util.traverser;

public interface Traverser<T> {

	void traverse(T target, String currentPath, ObjectGraphTraverser mainTraverser);
	boolean supports(Class<?> clazz);
}
