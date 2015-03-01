package orest.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import orest.expression.registry.ExpressionMethodInformation.Join;


public class PredicateContext {
	
	private List<Join> joins = new ArrayList<Join>();
	
	
	public void addJoin(Join join){
		if(join != null)
			joins.add(join);
	}
	
	public void addJoins(List<Join> joins){
		if(joins != null)
			this.joins.addAll(joins);
	}
	
	public List<Join> getJoins(){
		return Collections.unmodifiableList(joins);
	}
	
	

}
