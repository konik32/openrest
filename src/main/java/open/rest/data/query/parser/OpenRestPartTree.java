package open.rest.data.query.parser;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.Assert;

import data.query.parser.PartTree;
import data.query.parser.TreeBranch;

public class OpenRestPartTree extends PartTree {

	private List<PropertyPath> expandPropertyPaths;

	private boolean propertyQuery = false;
	private String propertyName;

	public OpenRestPartTree(TreeBranch partTreeRoot, Sort sort, Boolean distinct, Boolean countProjection,List<PropertyPath> expandPropertyPaths) {
		super(partTreeRoot, sort, distinct, countProjection);
		this.expandPropertyPaths = expandPropertyPaths;
	}

	public OpenRestPartTree(TreeBranch partTreeRoot, Sort sort, Boolean distinct, Boolean countProjection,List<PropertyPath> expandPropertyPaths, String propertyName) {
		this(partTreeRoot, sort, distinct, countProjection,expandPropertyPaths);
		if (propertyName != null) {
			this.propertyName = propertyName;
			propertyQuery = true;
		}
	}

	public boolean isPropertyQuery() {
		return propertyQuery;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public List<PropertyPath> getExpandPropertyPaths() {
		return expandPropertyPaths;
	}

}
