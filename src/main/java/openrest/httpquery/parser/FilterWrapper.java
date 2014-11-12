package openrest.httpquery.parser;

import lombok.Data;

@Data
public class FilterWrapper {
	private TempPart tempPart;
	private String name;
	
	public FilterWrapper(TempPart tempPart, String name) {
		this.tempPart = tempPart;
		this.name = name;
	}
}
