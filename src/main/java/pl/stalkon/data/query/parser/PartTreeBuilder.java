package pl.stalkon.data.query.parser;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.stalkon.data.boost.httpquery.parser.TempPart;
import pl.stalkon.data.query.JpaParameter;
import pl.stalkon.data.query.parser.Part.Type;

public class PartTreeBuilder {

	private static Logger logger = LoggerFactory
			.getLogger(PartTreeBuilder.class);

	private static final Map<String, Type> PART_TYPES_MAP;
	static {
		Map<String, Type> typesMap = new HashMap<String, Type>();
		typesMap.put("between", Type.BETWEEN);
		typesMap.put("isNotNull", Type.IS_NOT_NULL);
		typesMap.put("isNull", Type.IS_NULL);
		typesMap.put("lt", Type.LESS_THAN);
		typesMap.put("le", Type.LESS_THAN_EQUAL);
		typesMap.put("gt", Type.GREATER_THAN);
		typesMap.put("ge", Type.GREATER_THAN_EQUAL);
		typesMap.put("before", Type.BEFORE);
		typesMap.put("after", Type.AFTER);
		typesMap.put("notLike", Type.NOT_LIKE);
		typesMap.put("like", Type.LIKE);
		typesMap.put("startingWith", Type.STARTING_WITH);
		typesMap.put("endingWith", Type.ENDING_WITH);
		typesMap.put("containing", Type.CONTAINING);
		typesMap.put("notIn", Type.NOT_IN);
		typesMap.put("in", Type.IN);
		typesMap.put("near", Type.NEAR);
		typesMap.put("within", Type.WITHIN);
		typesMap.put("regex", Type.REGEX);
		typesMap.put("exists", Type.EXISTS);
		typesMap.put("true", Type.TRUE);
		typesMap.put("false", Type.FALSE);
		typesMap.put("eq", Type.SIMPLE_PROPERTY);
		typesMap.put("notEq", Type.NEGATING_SIMPLE_PROPERTY);
		PART_TYPES_MAP = Collections.unmodifiableMap(typesMap);
	}

	private final Class<?> domainClass;
	private final ObjectMapper objectMapper;

	private final TreeBranch root = new AndBranch();
	private Boolean countProjection;
	private Boolean distinct;

	private List<JpaParameter> jpaParameters = new ArrayList<JpaParameter>();
	private List<Object> parametersValues = new ArrayList<Object>();

	public PartTreeBuilder(Class<?> domainClass, ObjectMapper objectMapper) {
		super();
		this.domainClass = domainClass;
		this.objectMapper = objectMapper;
	}

	public void append(TempPart tempPart, List<String[]> parameters) {
		root.addPart(getTreePart(tempPart));
		appendParametersValues(parameters);
	}

	public void appendId(String id) {
		root.addPart(new Part("id", Type.SIMPLE_PROPERTY, domainClass));
		Class<?> type = PropertyPath.from("id", domainClass).getLeafProperty().getType();
		jpaParameters.add(new JpaParameter(type, jpaParameters.size()));
		parametersValues.add(getParsedObject(id, type));
	}

	public void appendParentIdPredicate(Class<?> parentType,
			String propertyName, String id) {
		String domainPropertyName = retrieveDomainPropertyName(parentType,
				propertyName);
		String partName = domainPropertyName + ".id";
		root.addPart(new Part(partName, Type.SIMPLE_PROPERTY, domainClass));
		Class<?> type = PropertyPath.from(partName, domainClass).getLeafProperty().getType();
		jpaParameters.add(new JpaParameter(type, jpaParameters.size()));
		parametersValues.add(getParsedObject(id, type));
	}
	
	public PartTree getPartTree(){
		return new PartTree(root, null, distinct, countProjection);
	}

	private String retrieveDomainPropertyName(Class<?> parentType,
			String propertyName) throws ResourceNotFoundException {
		Field field;
		try {
			field = parentType.getDeclaredField(propertyName);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new ResourceNotFoundException();
		} catch (SecurityException e) {
			throw new ResourceNotFoundException();
		}

		for (Annotation ann : field.getAnnotations()) {
			if (ann.annotationType().equals(OneToMany.class)
					|| ann.annotationType().equals(OneToOne.class)) {
				String mappedBy = (String) AnnotationUtils.getValue(ann,
						"mappedBy");
				return mappedBy;
			} else if (ann.annotationType().equals(ManyToOne.class)) {
				for (Field f : field.getType().getDeclaredFields()) {
					Annotation oneToMany = AnnotationUtils.getAnnotation(f,
							OneToMany.class);
					if (oneToMany != null) {
						String mappedBy = (String) AnnotationUtils.getValue(
								oneToMany, "mappedBy");
						if (mappedBy != null
								&& mappedBy.compareToIgnoreCase(propertyName) == 0)
							return f.getName();
					}
				}

			}
		}
		throw new ResourceNotFoundException();
	}

	public void setCountProjection() {
		countProjection = true;
	}

	public void setDistinct() {
		distinct = true;
	}

	private void appendParametersValues(List<String[]> parameters) {
		int i = parametersValues.size();
		for (String[] params : parameters) {
			for (String param : params) {
				parametersValues.add(getParsedObject(param,
						jpaParameters.get(i++).getType()));
			}
		}
	}

	private TreePart getTreePart(TempPart tempPart) {
		TreePart part;
		switch (tempPart.getType()) {
		case AND:
			part = new AndBranch(tempPart.getParts().size());
			for (TempPart tPart : tempPart.getParts()) {
				((TreeBranch) part).addPart(getTreePart(tPart));
			}
			return part;
		case OR:
			part = new OrBranch(tempPart.getParts().size());
			for (TempPart tPart : tempPart.getParts()) {
				((TreeBranch) part).addPart(getTreePart(tPart));
			}
			return part;
		case LEAF:
			part = new Part(tempPart.getPropertyName(),
					PART_TYPES_MAP.get(tempPart.getFunctionName()), domainClass);

			Class<?> partPropertyType = ((Part) part).getProperty()
					.getLeafProperty().getType();
			for (int i = 0; i < tempPart.getParametersCount(); i++)
				jpaParameters.add(new JpaParameter(partPropertyType,
						jpaParameters.size()));
			return part;
		}

		// should never get here
		return null;
	}

	private Object getParsedObject(String sValue, Class<?> type) {
		try {
			return objectMapper.readValue("\"" + sValue + "\"", type);
		} catch (JsonParseException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException(e.getMessage());
		} catch (JsonMappingException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public List<JpaParameter> getJpaParameters() {
		return jpaParameters;
	}

	public List<Object> getParametersValues() {
		return parametersValues;
	}

	public Class<?> getDomainClass() {
		return domainClass;
	}
}
