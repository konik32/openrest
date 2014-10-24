package pl.stalkon.data.boost.httpquery.parser;

import java.util.List;

import lombok.Data;

@Data
public class FilterWrapper {
	private TempPart tempPart;
	private List<String[]> values;
	private String name;
	
	public FilterWrapper(TempPart tempPart, List<String[]> values) {
		this.tempPart = tempPart;
		this.values = values;
		this.name = null;
	}
	
	public FilterWrapper(TempPart tempPart, List<String[]> values, String name) {
		this.tempPart = tempPart;
		this.values = values;
	}
}
