package pl.stalkon.data.boost.httpquery.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.Getter;

import org.springframework.util.Assert;

public class FilterParser {
	private static final String OR_SPLITTER = "(?i);or;";
	private static final String AND_SPLITTER = "(?i);and;";
	private static final Pattern FUNCTION_PARAMETER_MATCHER = Pattern
			.compile("^(.*)\\((.*)\\)$");

	private @Getter List<String[]> parameters = new ArrayList<String[]>();
	private final String filterStr;
	private @Getter TempPart tempRoot = new TempPart(TempPart.Type.AND, 1);
	
	private @Getter boolean parsed = false;

	public FilterParser(String filterStr) {
		super();
		this.filterStr = filterStr;
	}

	public void parse() {
		if(filterStr == null) return;
		tempRoot.addPart(parseOrBranch(filterStr));
		parsed = true;
	}

	private TempPart parseOrBranch(String sOrBranch) {
		String sOrParts[] = sOrBranch.split(OR_SPLITTER);

		TempPart orBranch = new TempPart(TempPart.Type.OR, sOrParts.length);
		for (String sOrPart : sOrParts) {
			orBranch.addPart(parseAnd(sOrPart.trim()));
		}
		return orBranch;
	}

	private TempPart parseAnd(String sAndBranch) {
		String sAndParts[] = sAndBranch.split(AND_SPLITTER);
		TempPart andBranch = new TempPart(TempPart.Type.AND, sAndParts.length);
		for (String sAndPart : sAndParts) {
			andBranch.addPart(parsePartString(sAndPart.trim()));
		}
		return andBranch;
	}

	private TempPart parsePartString(String partStr) {
		Matcher matcher = FUNCTION_PARAMETER_MATCHER.matcher(partStr);

		Assert.state(matcher.find(), "Filter is not correctly formed");

		String functionName = matcher.group(1).trim();
		String functionParams[] = matcher.group(2).split(",");

		TempPart part = new TempPart(functionName, functionParams[0],
				functionParams.length - 1);
		parameters.add(Arrays.copyOfRange(functionParams, 1,
				functionParams.length));
		return part;
	}

}
