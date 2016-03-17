package pl.openrest.core.webmvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class OpenRestAwareRootResourceInformationHandlerMethodArgumentResolver extends RootResourceInformationHandlerMethodArgumentResolver {

    @Autowired
    private List<RepositoryInvokerResolver> repositoryInvokerResolvers;
    private Repositories repositories;

    public OpenRestAwareRootResourceInformationHandlerMethodArgumentResolver(Repositories repositories,
            RepositoryInvokerFactory invokerFactory, ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver) {
        super(repositories, invokerFactory, resourceMetadataResolver);
        this.repositories = repositories;
    }

    @Override
    public RootResourceInformation resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        RootResourceInformation resourceInformation = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        RepositoryInvoker invoker = resourceInformation.getInvoker();
        Object repository = repositories.getRepositoryFor(resourceInformation.getDomainType());
        for (RepositoryInvokerResolver resolver : repositoryInvokerResolvers) {
            if (resolver.support(resourceInformation.getDomainType(), webRequest)) {
                invoker = resolver.resolve(resourceInformation, repository, parameter, webRequest);
                break;
            }
        }
        return resourceInformation = new RootResourceInformation(resourceInformation.getResourceMetadata(),
                resourceInformation.getPersistentEntity(), invoker);

    }

    public void setRepositoryInvokerResolvers(List<RepositoryInvokerResolver> repositoryInvokerResolvers) {
        this.repositoryInvokerResolvers = repositoryInvokerResolvers;
    }

}
