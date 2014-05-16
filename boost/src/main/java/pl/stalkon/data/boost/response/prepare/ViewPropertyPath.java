package pl.stalkon.data.boost.response.prepare;

import org.springframework.data.mapping.PropertyPath;

public class ViewPropertyPath {

	private PropertyPath propertyPath;
	private PropertyPath curr;
	private String value;
	
	
	public ViewPropertyPath(String source, Class<?> type, String value){
		propertyPath = PropertyPath.from(source, type);
		reset();
		this.value = value;
	}
	
	public ViewPropertyPath(String source, Class<?> type){
		this(source, type, null);
	}

	public ViewPropertyPath next() {
		curr = curr.next();
		return this;
	}
	
	public boolean hasNext(){
		return curr.hasNext();
	}
	
	public void reset(){
		curr = propertyPath;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public PropertyPath getCurrentPropertyPath(){
		return curr;
	}

}
