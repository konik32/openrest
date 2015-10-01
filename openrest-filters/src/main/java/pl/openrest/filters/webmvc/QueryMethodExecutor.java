package pl.openrest.filters.webmvc;

import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.util.MultiValueMap;

public interface QueryMethodExecutor {
    Object executeQueryMethod(RootResourceInformation resourceInformation, MultiValueMap<String, Object> parameters, String search,
            DefaultedPageable pageable, Sort sort);
}
