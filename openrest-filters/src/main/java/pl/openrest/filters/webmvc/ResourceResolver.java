package pl.openrest.filters.webmvc;

import java.io.Serializable;

import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.util.MultiValueMap;

public interface ResourceResolver {

    Iterable<Object> getCollectionResource(RootResourceInformation resourceInformation, MultiValueMap<String, Object> parameters,
            DefaultedPageable pageable, Sort sort);

    Object getItemResource(RootResourceInformation resourceInformation, MultiValueMap<String, Object> parameters, Serializable id);


}
