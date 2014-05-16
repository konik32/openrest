package org.springframework.data.query.parser;

import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;

public class OrBranch extends TreeBranch {
	
	public OrBranch(List<TreePart> treeParts) {
		super(treeParts);
	}
	@Override
	public String toString() {

		return StringUtils.collectionToDelimitedString(treeParts, " or ");
	}
}


