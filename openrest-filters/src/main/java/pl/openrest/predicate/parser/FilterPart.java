package pl.openrest.predicate.parser;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FilterPart {

    public enum FilterPartType {
        AND, OR, LEAF
    };

    private List<FilterPart> parts;
    private String predicate;
    private FilterPartType type;

    public FilterPart(String predicate) {
        this.predicate = predicate;
        type = FilterPartType.LEAF;
    }

    public FilterPart(FilterPartType type) {
        parts = new ArrayList<FilterPart>();
        this.type = type;
    }

    public void addPart(FilterPart filterPart) {
        parts.add(filterPart);
    }

}