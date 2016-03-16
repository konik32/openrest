package pl.openrest.filters.query;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class JoinInformation<T> {
    protected final T path;
    protected final boolean fetch;

}