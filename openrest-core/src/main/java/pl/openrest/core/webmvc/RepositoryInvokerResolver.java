package pl.openrest.core.webmvc;

import org.springframework.core.MethodParameter;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.web.context.request.NativeWebRequest;

public interface RepositoryInvokerResolver {

    RepositoryInvoker resolve(RootResourceInformation information, Object repository, MethodParameter parameter,
            NativeWebRequest webRequest);

    boolean support(Class<?> domainType, NativeWebRequest webRequest);
}
