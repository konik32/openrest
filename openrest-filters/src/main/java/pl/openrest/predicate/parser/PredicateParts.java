package pl.openrest.predicate.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class PredicateParts {

    private final String predicateName;
    private final String parameters[];
}
