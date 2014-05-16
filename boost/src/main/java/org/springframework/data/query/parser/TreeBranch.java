package org.springframework.data.query.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.query.parser.Part.Type;

public abstract class TreeBranch implements TreePart{
	
	protected final List<TreePart> treeParts;
	
	public TreeBranch(List<TreePart> treeParts) {
		this.treeParts = treeParts;
	}

	@Override
	public List<Part> getParts(){
		List<Part> parts = new ArrayList<Part>();
		for(TreePart part: treeParts){
			parts.addAll(part.getParts());
		}
		return parts;
	}
	
	@Override
	public List<Part> getParts(Type type){
		List<Part> parts = new ArrayList<Part>();
		for(TreePart part: treeParts){
			parts.addAll(part.getParts(type));
		}
		return parts;
	}
	
}
