package openrest.dto;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.web.context.request.ServletWebRequest;

import openrest.webmvc.PersistentEntityWithAssociationsResourceAssembler;
import openrest.webmvc.PersistentEntityWithAssociationsResourceAssemblerArgumentResolver;

public class EmbeddedWrapperFactory implements InitializingBean {

	@Autowired
	private HttpServletRequest request;

	private EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
	private PersistentEntityWithAssociationsResourceAssembler assembler;
	private PersistentEntityWithAssociationsResourceAssemblerArgumentResolver assemblerResolver;

	public EmbeddedWrapperFactory(PersistentEntityWithAssociationsResourceAssemblerArgumentResolver assemblerResolver) {
		this.assemblerResolver = assemblerResolver;
	}

	public EmbeddedWrapper wrap(Object source, String rel, boolean isResource) {
		if (!isResource || source instanceof Collection)
			return wrappers.wrap(source, rel);
		return wrappers.wrap(assembler.toFullResource(source), rel);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assembler = assemblerResolver.getResourceAssembler(new ServletWebRequest(request));
	}

}
