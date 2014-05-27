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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.query.JpaParameter;
import org.springframework.data.query.JpaParameters;
import org.springframework.data.query.parser.AndBranch;
import org.springframework.data.query.parser.OrBranch;
import org.springframework.data.query.parser.Part;
import org.springframework.data.query.parser.PartTree;
import org.springframework.data.query.parser.Part.Type;
import org.springframework.data.query.parser.TreePart;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parsers {

	private static Logger logger = LoggerFactory.getLogger(Parsers.class);

	private EntityTypeExctractor domainClassExtractor;

	private ObjectMapper objectMapper;

	private IdChecker idChecker;

	private String uriPrefix;

	private String idPropertyName = "id";

	private static final String OR_SPLITTER = "(?i) ;or; ";
	private static final String AND_SPLITTER = "(?i) ;and; ";
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

	public Parsers() {

	}

	public Parsers(IdChecker idChecker, ObjectMapper objectMapper,
			String idPropertyName, String uriPrefix,
			EntityTypeExctractor domainClassExtractor) {
		super();
		this.idChecker = idChecker;
		this.objectMapper = objectMapper;
		this.idPropertyName = idPropertyName;
		this.uriPrefix = uriPrefix;
		this.domainClassExtractor = domainClassExtractor;
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

	private void addPart(List<JpaParameter> jpaParameters,
			List<Object> parametersValues, List<TreePart> treeParts,
			Class<?> domainClass, Type partType, String name,
			int parametersIndex, String... values) {
		Part part = new Part(name, partType, domainClass);
		Class<?> type = part.getProperty().getLeafProperty().getType();
		treeParts.add(part);
		for (int i = 0; i < values.length; i++) {
			jpaParameters.add(new JpaParameter(type, parametersIndex++));
			parametersValues.add(getParsedObject(values[i], type));
		}
	}

	public List<IdEntry> getIds(String source) {
		String uriParts[] = source.replace(uriPrefix + "/", "").split("/");
		List<IdEntry> ids = new ArrayList<IdEntry>(
				(int) Math.ceil((float) uriParts.length / 2.0f));
		for (int i = 0; i < uriParts.length; i += 2) {
			String name = uriParts[i];
			try {
				String id = uriParts[i + 1];
				Assert.state(idChecker.isId(id),
						"Invalid uri. Uri should have following syntax: entityName/id/entityname/id...");
				ids.add(new IdEntry(name, id));
			} catch (IndexOutOfBoundsException e) {
				ids.add(new IdEntry(name, null));
			}
		}
		return ids;
	}

	public ParsedUri parseUri(String source, int parametersIndex) {

		List<IdEntry> ids = getIds(source);
		IdEntry rootEntry = ids.get(ids.size() - 1);
		Class<?> domainClass = getDomainClass(ids);
		int parametersCount = rootEntry.id != null ? ids.size()
				: ids.size() - 1;
		List<TreePart> treeParts = new ArrayList<TreePart>(parametersCount);
		List<JpaParameter> jpaParameters = new ArrayList<JpaParameter>(
				parametersCount);
		List<Object> parametersValues = new ArrayList<Object>(parametersCount);

		if (rootEntry.id != null) {
			addPart(jpaParameters, parametersValues, treeParts, domainClass,
					Type.SIMPLE_PROPERTY, idPropertyName, parametersIndex++,
					rootEntry.id);
		}
		String path = "";
		for (int i = ids.size() - 2; i >= 0; i--) {
			IdEntry entry = ids.get(i);
			Assert.state(idChecker.isId(entry.id),
					"Invalid uri. Uri should have following syntax: entityName/id/entityname/id...");
			path += entry.entityName;
			addPart(jpaParameters, parametersValues, treeParts, domainClass,
					Type.SIMPLE_PROPERTY, path + "." + idPropertyName,
					parametersIndex++, entry.id);
			path += ".";
		}
		return new ParsedUri(treeParts, domainClass, jpaParameters,
				parametersValues);
	}

	private Class<?> getDomainClass(List<IdEntry> ids) {
		IdEntry rootEntry = ids.get(ids.size() - 1);
		return domainClassExtractor.getEntityType(rootEntry.entityName);
	}
	
	public Class<?> getDomainClass(String uri){
		return getDomainClass(getIds(uri));
	}

	public ParsedQueryParameters parseQueryParameters(String uri,
			String filter, String subject, String sOrder, String sPageable,
			String views) {
		Assert.notNull(filter);
		Assert.notNull(uri);
		List<JpaParameter> jpaParametersList = new ArrayList<JpaParameter>();
		List<Object> parametersValues = new ArrayList<Object>();
		AndBranch mainAndBranch = new AndBranch();
		int parametersIndex = 0;

		// Parse uri
		ParsedUri parsedUri = parseUri(uri, parametersIndex);
		parametersIndex += parsedUri.jpaParameters.size();
		jpaParametersList.addAll(parsedUri.jpaParameters);
		parametersValues.addAll(parsedUri.parametersValues);
		mainAndBranch.addAll(parsedUri.parts);

		// Parse filter
		if (!filter.isEmpty()) {
			List<Branch> branches = parseFilter(filter, parsedUri.domainClass,
					parametersIndex, objectMapper);
			OrBranch orBranch = new OrBranch();

			for (Branch branch : branches) {
				jpaParametersList.addAll(branch.getJpaParameters());
				parametersValues.addAll(branch.getParametersValues());
				orBranch.addPart(new AndBranch(branch.getParts()));
				parametersIndex += branch.getParametersValues().size();
			}
			mainAndBranch.addPart(orBranch);
		}

		// Parse pageable and Sort
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

		// Create partTree
		PartTree partTree = new PartTree(mainAndBranch, null,
				parseDistinct(subject), parseCount(subject));
		List<PropertyPath> viewPropertyPaths = parseViews(views,
				parsedUri.domainClass);
		return new ParsedQueryParameters(partTree, new JpaParameters(
				jpaParametersList, sortIndex, pageableIndex),
				parametersValues.toArray(), viewPropertyPaths,
				(Class<Object>) parsedUri.domainClass);
	}

	private boolean parseCount(String subject) {
		return subject == null ? false : subject.matches("(?i)count");

	}

	private boolean parseDistinct(String subject) {
		return subject == null ? false : subject.matches("(?i)distinct");
	}

	private List<PropertyPath> parseViews(String views, Class<?> domainClass) {
		if (views == null)
			return null;
		String parts[] = views.split(",");
		List<PropertyPath> viewsPropertyPaths = new ArrayList<PropertyPath>(
				parts.length);
		for (String part : parts) {
			viewsPropertyPaths.add(PropertyPath.from(part, domainClass));
		}
		return viewsPropertyPaths;
	}

	public Sort parseSort(String order) {
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

	public Pageable parsePageable(String pageable, String order) {
		if (pageable == null)
			return null;
		String parts[] = pageable.split(";");
		return new PageRequest(new Integer(parts[0]), new Integer(parts[1]),
				order != null ? parseSort(order) : null);
	}

	private List<Branch> parseFilter(String filter, Class<?> domainClass,
			int parametersIndex, ObjectMapper objectMapper) {
		String sOrParts[] = filter.split(OR_SPLITTER);
		List<Branch> branches = new ArrayList<Branch>(sOrParts.length);
		for (String sOrPart : sOrParts) {
			Branch branch = parseBranch(sOrPart.trim(), domainClass,
					parametersIndex, objectMapper);
			parametersIndex += branch.getParametersValues().size();
			branches.add(branch);
		}
		return branches;
	}

	private Branch parseBranch(String source, Class<?> domainClass,
			int parametersIndex, ObjectMapper objectMapper) {
		String sParts[] = source.split(AND_SPLITTER);
		List<TreePart> parts = new ArrayList<TreePart>(sParts.length);
		List<JpaParameter> jpaParameters = new ArrayList<JpaParameter>();
		List<Object> parametersValues = new ArrayList<Object>();
		for (String sPart : sParts) {
			Leaf leaf = parseLeaf(sPart.trim(), domainClass, parametersIndex,
					objectMapper);
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

	private Leaf parseLeaf(String source, Class<?> domainClass,
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
					parametersIndex++);
			Object parametersValues[] = new Object[parameters.length - 1];
			for (int i = 1; i < parameters.length; i++) {
				parametersValues[i - 1] = getParsedObject(parameters[i],
						parameterType);
			}
			return new Leaf(part, parametersValues, jpaParameter);
		} else {
			return new Leaf(part, new Object[0], null);
		}

	}

	public static class IdEntry {
		public final String entityName;
		public final String id;

		public IdEntry(String entityName, String id) {
			this.entityName = entityName;
			this.id = id;
		}
	}

	private static class Branch {
		private final List<TreePart> parts;
		private final List<JpaParameter> jpaParameters;
		private final List<Object> parametersValues;

		public Branch(List<TreePart> parts, List<JpaParameter> jpaParameters,
				List<Object> parametersValues) {
			this.parts = parts;
			this.jpaParameters = jpaParameters;
			this.parametersValues = parametersValues;
		}

		public List<TreePart> getParts() {
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
		private final TreePart part;
		private final Object parametersValues[];
		private final JpaParameter jpaParameter;

		public Leaf(TreePart part, Object[] parametersValues,
				JpaParameter jpaParameter) {
			this.part = part;
			this.parametersValues = parametersValues;
			this.jpaParameter = jpaParameter;
		}

		public TreePart getPart() {
			return part;
		}

		public Object[] getParametersValues() {
			return parametersValues;
		}

		public JpaParameter getJpaParameter() {
			return jpaParameter;
		}

	}

	public static class ParsedQueryParameters {
		private PartTree partTree;
		private JpaParameters jpaParameters;
		private Object parametersValues[];
		private List<PropertyPath> viewPropertyPaths;
		private Class<Object> domainClass;

		public ParsedQueryParameters() {

		}

		public ParsedQueryParameters(PartTree partTree,
				JpaParameters jpaParameters, Object[] parametersValues,
				List<PropertyPath> viewPropertyPaths, Class<Object> domainClass) {
			super();
			this.partTree = partTree;
			this.jpaParameters = jpaParameters;
			this.parametersValues = parametersValues;
			this.viewPropertyPaths = viewPropertyPaths;
			this.domainClass = domainClass;
		}

		public Class<Object> getDomainClass() {
			return domainClass;
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

		public List<PropertyPath> getViewPropertyPaths() {
			return viewPropertyPaths;
		}

	}

	public static class ParsedUri {

		private final List<TreePart> parts;
		private final Class<?> domainClass;
		private final List<JpaParameter> jpaParameters;
		private final List<Object> parametersValues;

		public ParsedUri(List<TreePart> parts, Class<?> domainClass,
				List<JpaParameter> jpaParameters, List<Object> parametersValues) {
			super();
			this.parts = parts;
			this.domainClass = domainClass;
			this.jpaParameters = jpaParameters;
			this.parametersValues = parametersValues;
		}

		public List<TreePart> getParts() {
			return parts;
		}

		public Class<?> getDomainClass() {
			return domainClass;
		}

		public List<JpaParameter> getJpaParameters() {
			return jpaParameters;
		}

		public List<Object> getParametersValues() {
			return parametersValues;
		}

	}
}
