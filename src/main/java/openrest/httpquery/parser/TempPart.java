package openrest.httpquery.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.ToString;

@ToString
public class TempPart {

	public enum Type {
		AND, OR, LEAF
	};

	private static final Pattern IGNORE_CASE = Pattern.compile("IgnoreCase");

	private List<TempPart> parts;                    
	private String functionName;
	private String propertyName;
	private List<String> tempParameters;

	private boolean ignoreCase;
	private Type type;

	public TempPart() {
		this.tempParameters = new ArrayList<String>();
		this.parts = new ArrayList<TempPart>();
	}

	public TempPart(String functionName, String propertyName, String[] parameters) {
		super();
		this.functionName = detectAndSetIgnoreCase(functionName);
		this.propertyName = propertyName;
		this.tempParameters = Arrays.asList(parameters);
		this.type = Type.LEAF;
	}

	private String detectAndSetIgnoreCase(String functionName) {

		Matcher matcher = IGNORE_CASE.matcher(functionName);
		String result = functionName;

		if (matcher.find()) {
			this.ignoreCase = true;
			result = functionName.substring(0, matcher.start()) + functionName.substring(matcher.end(), functionName.length());
		}

		return result;
	}

	public TempPart(Type type, int size) {
		this.type = type;
		this.parts = new ArrayList<TempPart>(size);
	}

	public TempPart(Type type) {
		this.type = type;
		this.parts = new ArrayList<TempPart>();
	}

	public void addPart(TempPart part) {
		if (part != null)
			parts.add(part);
	}

	public List<TempPart> getParts() {
		return parts;
	}

	public String getFunctionName() {
		return functionName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Type getType() {
		return type;
	}

	public boolean shouldIgnoreCase() {
		return ignoreCase;
	}

	public String[] getParameters() {

		return tempParameters.toArray(new String[] {});
	}

	public void setParts(List<TempPart> parts) {
		this.parts = parts;
	}

	public void setFunctionName(String functionName) {
		this.functionName = detectAndSetIgnoreCase(functionName);
	}

	public void addParameter(String parameter) {
		tempParameters.add(parameter);
	}

	public void setParameters(String[] parameters) {
		tempParameters = Arrays.asList(parameters);
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}
