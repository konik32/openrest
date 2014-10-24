package pl.stalkon.data.boost.httpquery.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.Getter;

import org.eclipse.jetty.util.StringUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class FilterParser {
	private static final String OR_SPLITTER = ";or;";
	private static final String AND_SPLITTER = ";and;";
	private static final Pattern FUNCTION_PARAMETER_MATCHER = Pattern.compile("^(.*)\\((.*)\\)$");

	private @Getter List<String[]> parameters = new ArrayList<String[]>();
	private final String filterStr;
	private @Getter TempPart tempRoot = new TempPart(TempPart.Type.AND, 1);

	private @Getter boolean parsed = false;

	public FilterParser(String filterStr) {
		super();
		this.filterStr = filterStr;
	}

	public FilterWrapper parse() {
		if (filterStr == null)
			return null;
		tempRoot.addPart(parseOrBranch(StringUtils.trimAllWhitespace(filterStr)));
		parsed = true;
		return new FilterWrapper(tempRoot, parameters);
	}
	
	private TempPart parseOrBranch(String sOrBranch) {
		String sOrParts[] = sOrBranch.split(OR_SPLITTER);
		if(sOrBranch.endsWith(OR_SPLITTER))
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
		if(sAndBranch.endsWith(AND_SPLITTER))
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
