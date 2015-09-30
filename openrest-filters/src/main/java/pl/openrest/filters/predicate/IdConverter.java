package pl.openrest.filters.predicate;

import java.io.Serializable;

import org.springframework.data.mapping.PersistentProperty;

public interface IdConverter {

    Object convert(PersistentProperty idProperty, Serializable idValue);
}
