package pl.stalkon.data.boost.httpquery.parser;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class TempPart {

	public enum Type {
		AND, OR, LEAF
	};

	private List<TempPart> parts;
	private String functionName;
	private String propertyName;
	private int parametersCount;
	private final Type type;

	public TempPart(String functionName, String propertyName, int parametersCount) {
		super();
		this.functionName = functionName;
		this.propertyName = propertyName;
		this.parametersCount = parametersCount;
		this.type = Type.LEAF;
	}

	public TempPart(Type type, int size) {
		this.type = type;
		this.parts = new ArrayList<TempPart>(size);
	}

	public TempPart(Type type) {
		this.type = type;
		this.parts = new ArrayList<TempPart>();
	}

//	public TreePart getTreePart(Class<?> domainClass) {
//		TreePart part;
//		switch (type) {
//		case AND:
//			part = new AndBranch(parts.size());
//			for (TempPart tPart : parts) {
//				((TreeBranch) part).addPart(tPart.getTreePart(domainClass));
//			}
//			return part;
//		case OR:
//			part = new OrBranch(parts.size());
//			for (TempPart tPart : parts) {
//				((TreeBranch) part).addPart(tPart.getTreePart(domainClass));
//			}
//			return part;
//		case LEAF:
//			part = new Part(propertyName, PART_TYPES_MAP.get(functionName),
//					domainClass);
//			return part;
//		}
//		
//		// should never get here
//		return null;
//	}

	public void addPart(TempPart part) {
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

	public int getParametersCount() {
		return parametersCount;
	}

}
