package pl.openrest.dto.webmvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.web.context.request.NativeWebRequest;

import pl.openrest.core.webmvc.RepositoryInvokerResolver;
import pl.openrest.dto.handler.DtoRequestContext;
import pl.openrest.dto.repository.DtoRepositoryInvokerAdapter;

public class DtoRepositoryInvokerResolver implements RepositoryInvokerResolver, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private DtoRequestContext dtoRequestContext;

    @Override
    public RepositoryInvoker resolve(RootResourceInformation information, Object repository, MethodParameter parameter,
            NativeWebRequest webRequest) {
        DtoRepositoryInvokerAdapter adapter = new DtoRepositoryInvokerAdapter(information.getInvoker());
        adapter.setDtoRequestContext(dtoRequestContext);
        adapter.setApplicationEventPublisher(applicationEventPublisher);
        return adapter;
    }

    @Override
    public boolean support(Class<?> domainType, NativeWebRequest webRequest) {
        return webRequest.getParameter("dto") != null;

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
