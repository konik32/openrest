package org.springframework.data.query.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.query.parser.Part.Type;

public abstract class TreeBranch implements TreePart{
	
	protected final List<TreePart> treeParts;
	
	public TreeBranch(List<TreePart> treeParts) {
		this.treeParts = treeParts;
	}
	
	public TreeBranch(){
		this.treeParts = new ArrayList<TreePart>();
	}
	
	public void addPart(TreePart part){
		treeParts.add(part);
	}
	
	public void addAll(Collection<TreePart> treeParts){
		this.treeParts.addAll(treeParts);
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
