package pl.stalkon.data.boost.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import pl.stalkon.data.boost.domain.PartTreeSpecification;
import pl.stalkon.data.boost.domain.PartTreeSpecificationFactory;
import pl.stalkon.data.boost.httpquery.parser.Parsers;
import pl.stalkon.data.boost.httpquery.parser.Parsers.ParsedQueryParameters;
import pl.stalkon.data.boost.repository.BoostJpaRepository;
import pl.stalkon.data.boost.response.Response;
import pl.stalkon.data.boost.response.filter.WrappedResponse;

@Controller
@RequestMapping("/api")
public class BoostMainController {

	@Autowired
	private PartTreeSpecificationFactory partTreeSpecificationFactory;

	@Autowired
	private BoostJpaRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Parsers parsers;

	@RequestMapping(value = "/**", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> get(HttpServletRequest request, Principal principal) {

		ParsedQueryParameters parsedParameters = getParsedQueryParameters(request);
		PartTreeSpecification spec = partTreeSpecificationFactory
				.getBoostSpecification(parsedParameters.getPartTree(),
						parsedParameters.getJpaParameters(),
						parsedParameters.getParametersValues(),
						parsedParameters.getViewPropertyPaths());
		Iterable<Object> fetchedData = repository.findAll(spec,
				parsedParameters.getDomainClass());
		return new ResponseEntity<Response>(new Response(fetchedData), HttpStatus.OK);
	}

	private ParsedQueryParameters getParsedQueryParameters(
			HttpServletRequest request) {

		String filter = request.getParameter("filter");
		String page = request.getParameter("page");
		String sort = request.getParameter("sort");
		String subject = request.getParameter("subject");
		String view = request.getParameter("view");

		String contextPath = request.getContextPath();
		String uri = request.getRequestURI();
		uri = uri.replace(contextPath, "");

		return parsers.parseQueryParameters(uri, filter, subject, sort, page,
				view);
	}

}
