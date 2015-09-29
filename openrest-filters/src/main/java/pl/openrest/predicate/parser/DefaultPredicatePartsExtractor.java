package pl.openrest.predicate.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class DefaultPredicatePartsExtractor implements PredicatePartsExtractor {

    private static final Pattern PARTS_PATTERN = Pattern.compile("(?<name>[^()]+)(?:\\((?<params>.+)\\))?");
    private static final String EMPTY_PARAMS[] = {};
    private static final String PARAMS_SPLITTER = ";";

    @Override
    public PredicateParts extractParts(String predicate) {
        if (StringUtils.isEmpty(predicate))
            throw new IllegalArgumentException("Predicate should not be empty");
        predicate = predicate.trim();
        Matcher matcher = PARTS_PATTERN.matcher(predicate);
        if (matcher.find()) {
            return new PredicateParts(matcher.group("name"), extractParameters(matcher.group("params")));
        } else
            throw new IllegalArgumentException(String.format("Predicate: %s has wrong format", predicate));
    }

    private String[] extractParameters(String paramsStr) {
        if (StringUtils.isEmpty(paramsStr))
            return EMPTY_PARAMS;
        return paramsStr.split(PARAMS_SPLITTER);
    }
}
