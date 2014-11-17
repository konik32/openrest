package openrest.httpquery.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import openrest.antlr.ORLLexer;
import openrest.antlr.ORLParser;
import openrest.antlr.ORLParserListener;
import openrest.antlr.SyntaxErrorListener;
import openrest.domain.PartTreeSpecificationBuilder;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Set of parsers that parse strings into help objects used to create
 * {@link PartTreeSpecificationImpl}
 * 
 * @author Szymon Konicki
 *
 */

public class Parsers {

	/**
	 * Parses <b>filterStr</b> into {@link TempPart}
	 * 
	 * @param filterStr
	 *            should consist functions: <br/>
	 *            <b>function_name(resource_property_name,
	 *            resource_property_values ...)</b> <br/>
	 *            delimited by logical operators: <br/>
	 *            <b> ;and; ;or;</b> <br/>
	 *            For
	 *            function names see {@link PartTreeSpecificationBuilder} <br/>
	 *            <b>examples:</b> <br/>
	 *            <b> eq(id,1) ;and; between(product.price,1.50,2.00) ;or;
	 *            like(name,'GSM')</b>
	 * 
	 * @return {@link TempPart}
	 */

	public static TempPart parseFilter(String filterStr) {
		Assert.notNull(filterStr);
		ANTLRInputStream input = new ANTLRInputStream(filterStr);
		ORLLexer lexer = new ORLLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        ORLParser parser = new ORLParser(tokens);
        parser.addErrorListener(new SyntaxErrorListener());
        ParseTreeWalker walker = new ParseTreeWalker();
        ORLParserListener parserListener = new ORLParserListener();
        walker.walk(parserListener, parser.logicalExpression());
		return parserListener.getRoot();
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
}
