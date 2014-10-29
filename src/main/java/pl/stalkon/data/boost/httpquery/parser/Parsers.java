package pl.stalkon.data.boost.httpquery.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import data.query.parser.PartTreeSpecificationBuilder;

/**
 * Set of parsers that parse strings into help objects used to create
 * {@link PartTreeSpecification}
 * 
 * @author Szymon Konicki
 *
 */

public class Parsers {

	/**
	 * Parses <b>filterStr</b> into {@link FilterWrapper}
	 * 
	 * @param filterStr
	 *            should consist functions: <br/>
	 *            <b>function_name(resource_property_name,
	 *            resource_property_values ...)</b> <br/>
	 *            delimited by logical operators: <br/>
	 *            <b> ;and; ;or;</b> <br/>
	 *            Right now parsing extra brackets is not implemented. For
	 *            function names see {@link PartTreeSpecificationBuilder} <br/>
	 *            <b>examples:</b> <br/>
	 *            <b> eq(id,1) ;and; between(product.price,1.50,2.00) ;or;
	 *            like(name,GSM)</b>
	 * 
	 * @return {@link FilterWrapper}
	 */

	public static FilterWrapper parseFilter(String filterStr) {
		// TODO: add parsing extra brackets
		return new FilterParserWithContext().parse(StringUtils.trimAllWhitespace(filterStr));
	}

	/**
	 * Parses <b>expand</b> string into {@link List} of {@link PropertyPath}s
	 * 
	 * @param expand
	 *            format: <b>association_property_name,
	 *            association_property_name.association_property_name</b>
	 * @param domainType
	 *            must not be null
	 * @return {@link List} of {@link PropertyPath}s
	 */
	public static List<PropertyPath> parseExpand(String expand, Class<?> domainType) {
		Assert.notNull(domainType);
		if (expand == null || expand.isEmpty())
			return null;
		String parts[] = StringUtils.trimAllWhitespace(expand).split(",");
		List<PropertyPath> viewsPropertyPaths = new ArrayList<PropertyPath>(parts.length);
		for (String part : parts) {
			part = part.trim();
			viewsPropertyPaths.add(PropertyPath.from(part, domainType));
		}
		return viewsPropertyPaths;
	}

	/**
	 * Checks if <b>subject</b> contains <b>count</b> or <b>distinct</b> and
	 * returns {@link SubjectWrapper}
	 * 
	 * @param subject
	 *            format: <b>count, distinct</b>
	 * @return {@link SubjectWrapper}
	 */
	public static SubjectWrapper parseSubject(String subject) {
		return new SubjectWrapper().parse(StringUtils.trimAllWhitespace(subject));
	}

	/**
	 * Parses <b>path</b> string into {@link PathWrapper}
	 * 
	 * @param path
	 *            format: <b>/resource/id/property</b>
	 * @return {@link PathWrapper}
	 */
	public static PathWrapper parsePath(String path) {
		return new PathWrapper().parse(StringUtils.trimAllWhitespace(path));
	}

	/**
	 * 
	 * @param sFilter
	 *            format: <b>static_filter_name_to_ignore,
	 *            static_filter_name_to_ignore</b>
	 * 
	 */
	public static String[] parseSFilter(String sFilter) {
		sFilter = StringUtils.trimAllWhitespace(sFilter);
		return sFilter == null ? null : sFilter.split(",");
	}

	public static class PathWrapper {
		private @Getter String id;
		private @Getter String property;

		public PathWrapper parse(String path) {
			Assert.notNull(path);
			String parts[] = path.substring(1).split("/");
			try {
				id = parts[1] == "" ? null : parts[1];
				property = parts[2] == "" ? null : parts[2];
			} catch (IndexOutOfBoundsException e) {
				// ignore
			}
			return this;
		}
	}

	public static class SubjectWrapper {

		private @Getter Boolean countProjection;
		private @Getter Boolean distinct;

		public SubjectWrapper parse(String subject) {
			if (subject == null)
				return null;
			String parts[] = subject.split(",");
			for (String part : parts) {
				part = part.trim();
				if (part.matches("count"))
					countProjection = true;
				else if (part.matches("distinct"))
					distinct = true;
			}
			return this;
		}
	}

	private static class FilterParserWithContext {
		private static final String OR_SPLITTER = ";or;";
		private static final String AND_SPLITTER = ";and;";
		private static final Pattern FUNCTION_PARAMETER_MATCHER = Pattern.compile("^(.*)\\((.*)\\)$");

		private @Getter List<String[]> parameters = new ArrayList<String[]>();
		private @Getter TempPart tempRoot = new TempPart(TempPart.Type.AND, 1);

		public FilterWrapper parse(String filterStr) {
			if (filterStr == null || filterStr.isEmpty())
				return null;
			tempRoot.addPart(parseOrBranch(filterStr));
			return new FilterWrapper(tempRoot, parameters);
		}

		private TempPart parseOrBranch(String sOrBranch) {
			String sOrParts[] = sOrBranch.split(OR_SPLITTER);
			if (sOrBranch.endsWith(OR_SPLITTER))
				throw new RequestParsingException("Exception in parsing filter parameter fragment " + sOrBranch);
			TempPart orBranch = new TempPart(TempPart.Type.OR, sOrParts.length);
			for (String sOrPart : sOrParts) {
				if (sOrPart.isEmpty())
					throw new RequestParsingException("Exception in parsing filter parameter fragment " + sOrBranch);
				orBranch.addPart(parseAnd(sOrPart.trim()));
			}
			return orBranch;
		}

		private TempPart parseAnd(String sAndBranch) {
			String sAndParts[] = sAndBranch.split(AND_SPLITTER);
			if (sAndBranch.endsWith(AND_SPLITTER))
				throw new RequestParsingException("Exception in parsing filter parameter fragment " + sAndBranch);
			TempPart andBranch = new TempPart(TempPart.Type.AND, sAndParts.length);
			for (String sAndPart : sAndParts) {
				if (sAndPart.isEmpty())
					throw new RequestParsingException("Exception in parsing filter parameter fragment " + sAndBranch);
				andBranch.addPart(parsePartString(sAndPart.trim()));
			}
			return andBranch;
		}

		private TempPart parsePartString(String partStr) {

			Matcher matcher = FUNCTION_PARAMETER_MATCHER.matcher(partStr);

			if (!matcher.find() || matcher.groupCount() != 2) {
				throw new RequestParsingException("Exception in parsing function " + partStr
						+ " .Correct function format: function_name(propertyName, parameters...) ");
			}
			String functionName = matcher.group(1).trim();
			String functionParams[] = matcher.group(2).split(",");

			if (functionParams.length < 1 || functionParams[0].isEmpty())
				throw new RequestParsingException("Exception in parsing function " + partStr
						+ " .Correct function format: function_name(propertyName, parameters...) ");

			TempPart part = new TempPart(functionName, functionParams[0], functionParams.length - 1);
			parameters.add(Arrays.copyOfRange(functionParams, 1, functionParams.length));
			return part;
		}
	}
}
