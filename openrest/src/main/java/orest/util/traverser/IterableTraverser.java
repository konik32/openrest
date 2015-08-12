package orest.util.traverser;

import java.util.Iterator;

public class IterableTraverser implements Traverser<Iterable> {

	@Override
	public void traverse(Iterable target, String currentPath, ObjectGraphTraverser mainTraverser) {
		Iterator it = target.iterator();
		int i = 0;
		while (it.hasNext()) {
			Object elem = it.next();
			String path = PathBuilder.appendTo(currentPath, i);
			mainTraverser.traverse(elem, path);
			i++;
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Iterable.class.isAssignableFrom(clazz);
	}

}
