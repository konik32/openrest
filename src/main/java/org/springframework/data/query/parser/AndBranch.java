package org.springframework.data.query.parser;

import java.util.List;

import org.springframework.util.StringUtils;

public class AndBranch extends TreeBranch {

	public AndBranch(List<TreePart> treeParts) {
		super(treeParts);
	}
	public AndBranch() {
		
	}

	@Override
	public String toString() {

		return StringUtils.collectionToDelimitedString(treeParts, " and ");
	}
}
