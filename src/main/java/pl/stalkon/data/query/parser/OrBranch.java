package pl.stalkon.data.query.parser;

import java.util.List;

import org.springframework.util.StringUtils;

public class OrBranch extends TreeBranch {
	
	public OrBranch(List<TreePart> treeParts) {
		super(treeParts);
	}
	
	public OrBranch() {
	}
	
	public OrBranch(int size) {
		super(size);
	}
	@Override
	public String toString() {

		return StringUtils.collectionToDelimitedString(treeParts, " or ");
	}
}


