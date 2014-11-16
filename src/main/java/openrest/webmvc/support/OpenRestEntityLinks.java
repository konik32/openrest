package openrest.webmvc.support;

import java.io.UnsupportedEncodingException;

import openrest.webmvc.ParsedRequestHandlerMethodArgumentResolver;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.rest.webmvc.util.UriUtils;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class OpenRestEntityLinks extends RepositoryEntityLinks {

	private ParsedRequestHandlerMethodArgumentResolver parsedRequestResolver;
	
	public OpenRestEntityLinks(Repositories repositories, ResourceMappings mappings, RepositoryRestConfiguration config,
			HateoasPageableHandlerMethodArgumentResolver resolver, PluginRegistry<BackendIdConverter, Class<?>> idConverters, ParsedRequestHandlerMethodArgumentResolver parsedRequestResolver) {
		super(repositories, mappings, config, resolver, idConverters);
		this.parsedRequestResolver = parsedRequestResolver;
	}
	
	@Override
	public Link linkToPagedResource(Class<?> type, Pageable pageable) {
		return getSelfLink(super.linkToPagedResource(type, pageable),true);
	}
	
	
	@Override
	public Link linkToSingleResource(Class<?> type, Object id) {
		return getSelfLink(super.linkToSingleResource(type, id),false);
	}
	
	public Link getSelfLink(Link baseLink, boolean isCollection){
		String href = baseLink.getHref().replaceAll("\\{.*\\}", "");
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(href).build();
		TemplateVariables templateVariables = parsedRequestResolver.getTemplateVariables(uriComponents, isCollection);
		templateVariables = templateVariables.concat(baseLink.getVariables());
		return new Link(new UriTemplate(href, templateVariables),baseLink.getRel());
	}
	
	public Link getSelfLink(String href, boolean isCollection){
		return getSelfLink(new Link(href), isCollection);
	}

}
