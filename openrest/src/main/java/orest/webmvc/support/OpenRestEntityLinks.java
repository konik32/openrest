package orest.webmvc.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariable.VariableType;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.hateoas.TemplateVariable.VariableType.REQUEST_PARAM;
import static org.springframework.hateoas.TemplateVariable.VariableType.REQUEST_PARAM_CONTINUED;

public class OpenRestEntityLinks extends RepositoryEntityLinks {

	public OpenRestEntityLinks(Repositories repositories, ResourceMappings mappings, RepositoryRestConfiguration config,
			HateoasPageableHandlerMethodArgumentResolver resolver, PluginRegistry<BackendIdConverter, Class<?>> idConverters) {
		super(repositories, mappings, config, resolver, idConverters);
	}

	@Override
	public Link linkToPagedResource(Class<?> type, Pageable pageable) {
		return getSelfLink(super.linkToPagedResource(type, pageable), true);
	}

	@Override
	public Link linkToSingleResource(Class<?> type, Object id) {
		return getSelfLink(super.linkToSingleResource(type, id), false);
	}

	public Link getSelfLink(Link baseLink, boolean isCollection) {
		String href = baseLink.getHref().replaceAll("\\{.*\\}", "");
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(href).build();
		TemplateVariables templateVariables = getTemplateVariables(uriComponents, isCollection);
		templateVariables = templateVariables.concat(baseLink.getVariables());
		return new Link(new UriTemplate(href, templateVariables), baseLink.getRel());
	}

	public Link getSelfLink(String href, boolean isCollection) {
		return getSelfLink(new Link(href), isCollection);
	}

	private TemplateVariables getTemplateVariables(UriComponents template, boolean isCollection) {
		List<TemplateVariable> names = new ArrayList<TemplateVariable>();
		MultiValueMap<String, String> queryParameters = template.getQueryParams();
		boolean append = !queryParameters.isEmpty();
		List<String> propertyNames = new ArrayList<String>(Arrays.asList("orest"));
		if (isCollection) {
			propertyNames.add("filters");
			propertyNames.add("count");
		}
		for (String propertyName : propertyNames) {
			if (!queryParameters.containsKey(propertyName)) {
				VariableType type = append ? REQUEST_PARAM_CONTINUED : REQUEST_PARAM;
				names.add(new TemplateVariable(propertyName, type));
			}
		}
		return new TemplateVariables(names);
	}

}
