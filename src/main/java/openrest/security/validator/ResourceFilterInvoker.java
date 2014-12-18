package openrest.security.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import openrest.webmvc.ParentAwareObject;

public class ResourceFilterInvoker {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private ResourceFilterRegister resourceFilterRegister;


	public boolean includeResource(Object resource, ParentAwareObject parent) {
		List<ResourceFilter<Object>> filters = resourceFilterRegister.getResourceFilters(resource.getClass());
		if (filters == null)
			return true;
		for (ResourceFilter<Object> filter : filters) {
			if (parent == null) {
				filter.validateMainResource(request, resource);
			} else {
				if (!filter.validateResource(request, resource, parent))
					return false;
			}
		}
		return true;
	}
}
