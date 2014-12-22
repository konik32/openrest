package openrest.query;

import openrest.httpquery.parser.TempPart;
import lombok.Data;

@Data
public class StaticFilterWrapper implements Cloneable {
	private TempPart tempPart;
	private String name;
	private String condition;

	public StaticFilterWrapper(TempPart tempPart, String name,String condition) {
		this.tempPart = tempPart;
		this.name = name;
		this.condition = condition;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
