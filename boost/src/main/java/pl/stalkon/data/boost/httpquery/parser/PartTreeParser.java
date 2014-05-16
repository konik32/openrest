package pl.stalkon.data.boost.httpquery.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.query.JpaParameter;
import org.springframework.data.query.JpaParameters;
import org.springframework.data.query.parser.OrBranch;
import org.springframework.data.query.parser.Part;
import org.springframework.data.query.parser.Part.Type;
import org.springframework.data.query.parser.PartTree;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PartTreeParser {

	private static Logger logger = LoggerFactory
			.getLogger(PartTreeParser.class);

	private static final String OR_SPLITTER = "(?i) or ";
	private static final String AND_SPLITTER = ";";
	private static final Pattern FUNCTION_PARAMETER_MATCHER = Pattern
			.compile("^(.*)\\((.*)\\)$");

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

	private PartTree partTree;
	private JpaParameters jpaParameters;
	private Object parametersValues[];

	public PartTreeParser(PartTree partTree, JpaParameters jpaParameters,
			Object[] parametersValues) {
		this.partTree = partTree;
		this.jpaParameters = jpaParameters;
		this.parametersValues = parametersValues;
	}

	public static PartTreeParser parse(String filter, String subject,
			String sOrder, String sPageable, Class<?> domainClass,
			ObjectMapper objectMapper) {
		Assert.notNull(filter);
		Assert.notNull(domainClass);
		Integer parametersIndex = 0;
		List<Branch> branches = parseFilter(filter, domainClass,
				parametersIndex, objectMapper);

		List<JpaParameter> jpaParametersList = new ArrayList<JpaParameter>();
		List<Object> parametersValues = new ArrayList<Object>();
		List<OrBranch> orParts = new ArrayList<OrBranch>(branches.size());
		for (Branch branch : branches) {
			jpaParametersList.addAll(branch.getJpaParameters());
			parametersValues.addAll(branch.getParametersValues());
			orParts.add(new OrBranch(branch.getParts()));
			parametersIndex += branch.getParametersValues().size();
		}
		Pageable pageable = parsePageable(sPageable, sOrder);
		int pageableIndex = -1;
		int sortIndex = -1;
		if (pageable != null) {
			parametersValues.add(pageable);
			pageableIndex = parametersIndex;
			jpaParametersList.add(new JpaParameter(Pageable.class,
					parametersIndex++));
		} else {
			Sort sort = parseSort(sOrder);
			parametersValues.add(sort);
			sortIndex = parametersIndex;
			jpaParametersList.add(new JpaParameter(Sort.class,
					parametersIndex++));
		}

		PartTree partTree = new PartTree(orParts, null, parseDistinct(subject),
				parseCount(subject));
		return new PartTreeParser(partTree, new JpaParameters(
				jpaParametersList, sortIndex, pageableIndex),
				parametersValues.toArray());
	}

	private static boolean parseCount(String subject) {
		return subject == null ? false : subject.matches("(?i)count");

	}

	private static boolean parseDistinct(String subject) {
		return subject == null ? false : subject.matches("(?i)distinct");
	}

	public static Sort parseSort(String order) {
		if (order == null)
			return null;
		Assert.notNull(order);
		String parts[] = order.split(";");
		Assert.state(
				parts.length == 2,
				"Order is not correctly formed. Check if it matches syntax properties...;directions...");
		String properties[] = parts[0].split(",");
		String directions[] = parts[1].split(",");
		Assert.state(directions.length == properties.length,
				"Properties count must be the same as directions count");
		List<Order> orders = new ArrayList<Order>(properties.length);
		for (int i = 0; i < properties.length; i++) {
			orders.add(new Order(Direction.fromStringOrNull(directions[i]),
					properties[i]));
		}
		return new Sort(orders);
	}

	public static Pageable parsePageable(String pageable, String order) {
		if (pageable == null)
			return null;
		String parts[] = pageable.split(";");
		return new PageRequest(new Integer(parts[0]), new Integer(parts[1]),
				order != null ? parseSort(order) : null);
	}

	private static List<Branch> parseFilter(String filter,
			Class<?> domainClass, int parametersIndex, ObjectMapper objectMapper) {
		String sOrParts[] = filter.split(OR_SPLITTER);
		List<Branch> branches = new ArrayList<Branch>(sOrParts.length);
		for (String sOrPart : sOrParts) {
			Branch branch = Branch.fromSource(sOrPart.trim(), domainClass,
					parametersIndex, objectMapper);
			parametersIndex += branch.getParametersValues().size();
			branches.add(branch);
		}
		return branches;
	}

	private static class Branch {
		private final List<Part> parts;
		private final List<JpaParameter> jpaParameters;
		private final List<Object> parametersValues;

		public Branch(List<Part> parts, List<JpaParameter> jpaParameters,
				List<Object> parametersValues) {
			this.parts = parts;
			this.jpaParameters = jpaParameters;
			this.parametersValues = parametersValues;
		}

		public static Branch fromSource(String source, Class<?> domainClass,
				int parametersIndex, ObjectMapper objectMapper) {
			String sParts[] = source.split(AND_SPLITTER);
			List<Part> parts = new ArrayList<Part>(sParts.length);
			List<JpaParameter> jpaParameters = new ArrayList<JpaParameter>();
			List<Object> parametersValues = new ArrayList<Object>();
			for (String sPart : sParts) {
				Leaf leaf = Leaf.fromSource(sPart.trim(), domainClass,
						parametersIndex, objectMapper);
				parts.add(leaf.getPart());
				if (leaf.getJpaParameter() != null) {
					jpaParameters.add(leaf.getJpaParameter());
					parametersValues.addAll(Arrays.asList(leaf
							.getParametersValues()));
					parametersIndex += leaf.getParametersValues().length;
				}
			}
			return new Branch(parts, jpaParameters, parametersValues);
		}

		public List<Part> getParts() {
			return parts;
		}

		public List<JpaParameter> getJpaParameters() {
			return jpaParameters;
		}

		public List<Object> getParametersValues() {
			return parametersValues;
		}
	}

	private static class Leaf {
		private final Part part;
		private final Object parametersValues[];
		private final JpaParameter jpaParameter;

		public Leaf(Part part, Object[] parametersValues,
				JpaParameter jpaParameter) {
			this.part = part;
			this.parametersValues = parametersValues;
			this.jpaParameter = jpaParameter;
		}

		public static Leaf fromSource(String source, Class<?> domainClass,
				int parametersIndex, ObjectMapper objectMapper) {
			Matcher matcher = FUNCTION_PARAMETER_MATCHER.matcher(source);
			Assert.state(matcher.find(), "Filter is not correctly formed");
			String function = matcher.group(1).trim();
			String parameters[] = matcher.group(2).split(",");

			Part part = new Part(parameters[0], PART_TYPES_MAP.get(function),
					domainClass);
			if (parameters.length > 1) {
				Class<?> parameterType = part.getProperty().getLeafProperty()
						.getType();
				JpaParameter jpaParameter = new JpaParameter(parameterType,
						parameters[0].replaceAll("\\.", "_"), parametersIndex++);

				Object parametersValues[] = new Object[parameters.length - 1];
				for (int i = 1; i < parameters.length; i++) {
					try {
						parametersValues[i - 1] = objectMapper.readValue("\""
								+ parameters[i] + "\"", parameterType);
					} catch (JsonParseException e) {
						logger.error(e.getMessage());
						throw new IllegalArgumentException(e.getMessage());
					} catch (JsonMappingException e) {
						logger.error(e.getMessage());
						throw new IllegalArgumentException(e.getMessage());
					} catch (IOException e) {
						logger.error(e.getMessage());
						e.printStackTrace();
					}
				}
				return new Leaf(part, parametersValues, jpaParameter);
			} else {
				return new Leaf(part, new Object[0], null);
			}

		}

		public Part getPart() {
			return part;
		}

		public Object[] getParametersValues() {
			return parametersValues;
		}

		public JpaParameter getJpaParameter() {
			return jpaParameter;
		}

	}

	public PartTree getPartTree() {
		return partTree;
	}

	public JpaParameters getJpaParameters() {
		return jpaParameters;
	}

	public Object[] getParametersValues() {
		return parametersValues;
	}

}
