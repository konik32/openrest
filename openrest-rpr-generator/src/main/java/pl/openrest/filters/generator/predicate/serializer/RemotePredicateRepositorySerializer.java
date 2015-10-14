package pl.openrest.filters.generator.predicate.serializer;

import java.io.IOException;
import java.util.List;

import pl.openrest.filters.generator.predicate.context.PredicateRepositoryInformation;

public interface RemotePredicateRepositorySerializer {

    public void serialize(List<PredicateRepositoryInformation> repositories) throws IOException;
}
