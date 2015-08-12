package orest.util.traverser;

import java.util.Iterator;
import java.util.Map;

public class MapTraverser implements Traverser<Map> {

	@Override
	public void traverse(Map target, String currentPath, ObjectGraphTraverser mainTraverser) {
		Iterator it = target.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			String path = PathBuilder.appendToMap(currentPath, key.toString());
			mainTraverser.traverse(target.get(key), path);
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

}
