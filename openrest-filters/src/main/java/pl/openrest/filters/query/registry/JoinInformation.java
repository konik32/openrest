package pl.openrest.filters.query.registry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.mysema.query.types.Path;

@Getter
@ToString
@EqualsAndHashCode
public class JoinInformation {
    private final Path<?> path;
    private final boolean collection;
    private final boolean fetch;
    private final Class<?> type;

    public JoinInformation(Path<?> path, boolean collection, boolean fetch, Class<?> type) {
        this.path = path;
        this.collection = collection;
        this.fetch = fetch;
        this.type = type;
    }
}