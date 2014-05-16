package org.springframework.data.query.parser;

import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;

public class OrPart implements Iterable<Part> {
	
	private final List<Part> children;

	public OrPart(List<Part> children) {
		this.children = children;
	}

	public Iterator<Part> iterator() {
		return children.iterator();
	}

	@Override
	public String toString() {

		return StringUtils.collectionToDelimitedString(children, " and ");
	}
}


