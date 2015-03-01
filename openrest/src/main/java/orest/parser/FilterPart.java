package orest.parser;

import java.util.ArrayList;
import java.util.List;

import orest.expression.registry.ExpressionMethodInformation;
import lombok.Data;

@Data
public class FilterPart {

	public enum FilterPartType {
		AND, OR, LEAF
	};

	private List<FilterPart> parts;
	private ExpressionMethodInformation methodInfo;
	private String[] parameters;
	private FilterPartType type;
	
	
	public FilterPart(ExpressionMethodInformation methodInfo,String[] parameters ){
		this.methodInfo = methodInfo;
		this.parameters = parameters;
		type = FilterPartType.LEAF;
	}
	
	public FilterPart(FilterPartType type){
		parts = new ArrayList<FilterPart>();
		this.type = type;
	}
	
	public void addPart(FilterPart filterPart){
		parts.add(filterPart);
	}
	
	

}