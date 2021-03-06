package pl.openrest.predicate.parser;

import lombok.NonNull;

import org.springframework.util.StringUtils;

import pl.openrest.predicate.parser.FilterPart.FilterPartType;

public class DefaultFilterTreeBuilder implements FilterTreeBuilder {

    private static final String OR_SPLITTER = ";or;";
    private static final String AND_SPLITTER = ";and;";

    private final PredicatePartsExtractor predicatePartsExtractor;

    public DefaultFilterTreeBuilder(@NonNull PredicatePartsExtractor predicatePartsExtractor) {
        this.predicatePartsExtractor = predicatePartsExtractor;
    }

    @Override
    public FilterPart from(String filters) {
        if (StringUtils.isEmpty(filters))
            return null;
        FilterPart filterOrPart = new FilterPart(FilterPartType.OR);
        for (String orPart : filters.split(OR_SPLITTER)) {
            FilterPart filterAndPart = new FilterPart(FilterPartType.AND);
            for (String andPart : orPart.split(AND_SPLITTER)) {
                filterAndPart.addPart(new FilterPart(predicatePartsExtractor.extractParts(andPart)));
            }
            filterOrPart.addPart(filterAndPart);
        }
        return filterOrPart;
    }
}
