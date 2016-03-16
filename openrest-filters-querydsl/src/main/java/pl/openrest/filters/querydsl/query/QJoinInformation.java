package pl.openrest.filters.querydsl.query;

import pl.openrest.filters.query.JoinInformation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.mysema.query.types.Path;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QJoinInformation extends JoinInformation<Path<?>> {
    private final boolean collection;
    private final Class<?> type;

    public QJoinInformation(Path<?> path, boolean fetch, boolean collection, Class<?> type) {
        super(path, fetch);
        this.collection = collection;
        this.type = type;
    }

}
