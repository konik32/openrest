package pl.openrest.predicate.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class DefaultPredicatePartsExtractor implements PredicatePartsExtractor {

    private static final Pattern PARTS_PATTERN = Pattern.compile("(?<name>[^()]+)(?:\\((?<params>.+)\\))?");
    private static final String EMPTY_PARAMS[] = {};
    private static final String PARAMS_SPLITTER = ";";
    private static final String PREDICATE_WRONG_FORMAT_MESSAGE = "Predicate: %s has wrong format";

    @Override
    public PredicateParts extractParts(String predicate) {
        if (StringUtils.isEmpty(predicate))
            throw new IllegalArgumentException("Predicate should not be empty");
        predicate = predicate.trim();
        Matcher matcher = PARTS_PATTERN.matcher(predicate);
        if (matcher.matches()) {
            return new PredicateParts(matcher.group("name"), extractParameters(matcher.group("params")));
        } else
            throw new IllegalArgumentException(String.format(PREDICATE_WRONG_FORMAT_MESSAGE, predicate));
    }

    private String[] extractParameters(String paramsStr) {
        if (StringUtils.isEmpty(paramsStr))
            return EMPTY_PARAMS;
        return paramsStr.split(PARAMS_SPLITTER);
    }
}
