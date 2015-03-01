package openrest.webmvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;

import openrest.httpquery.parser.Parsers;
import openrest.httpquery.parser.Parsers.PathWrapper;
import openrest.query.filter.StaticFilterFactory;
import openrest.query.parameter.ParameterProcessor;
import openrest.query.parameter.QueryParameterHolder;
import openrest.query.parameter.QueryParametersHolderBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory that builds and returns {@link ParsedRequest} from given parameters.
 * TODO add caching
 * 
 * @author Szymon Konicki
 *
 */
public class ParsedRequestFactory {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PersistentEntities persistentEntities;

	@Autowired
	private StaticFilterFactory staticaStaticFilterFactory;

	private ParameterProcessor parameterProcessor;

	/**
	 * Method uses {@link Parsers} to parse parameters and
	 * {@link QueryParametersHolderBuilder} to build
	 * {@link PartTreeSpecificationImpl} and wrap it into {@link ParsedRequest}.
	 * For parameters format see {@link Parsers}
	 * 
	 * @param filter
	 *            sql like string
	 * @param expand
	 *            associations to expand
	 * @param subject
	 *            count or distinct
	 * @param path
	 *            requested URI
	 * @param sFilter
	 *            string containing names of static filters to ignore
	 * @param domainClass
	 *            type of requested resource or parent resource in case path is
	 *            like /resource/id/property. Must not be {@literal null}
	 *
	 * @return {@link ParsedRequest}
	 **/
	public ParsedRequest getParsedRequest(String filter, String expand, String distinct, String count, String path, String dtos, Class<?> domainClass,
			Pageable pageable) {
		Assert.notNull(domainClass);

		PathWrapper pathWrapper = Parsers.parsePath(path);

		QueryParametersHolderBuilder partTreeSpecificationBuilder;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		if (pathWrapper.getProperty() != null) {
			Class<?> propertyType = PropertyPath.from(pathWrapper.getProperty(), domainClass).getType();
			partTreeSpecificationBuilder = new QueryParametersHolderBuilder(persistentEntities.getPersistentEntity(domainClass), objectMapper,
					staticaStaticFilterFactory);
			partTreeSpecificationBuilder.append(persistentEntities.getPersistentEntity(domainClass), pathWrapper.getProperty(), pathWrapper.getId());
		} else {
			partTreeSpecificationBuilder = new QueryParametersHolderBuilder(persistentEntities.getPersistentEntity(domainClass), objectMapper,
					staticaStaticFilterFactory);
			if (pathWrapper.getId() != null)
				partTreeSpecificationBuilder.append(pathWrapper.getId());
		}

		if (filter != null)
			partTreeSpecificationBuilder.append(Parsers.parseFilter(filter, false));
		if (distinct != null)
			partTreeSpecificationBuilder.setDistinct();
		if (count != null)
			partTreeSpecificationBuilder.setCountProjection();
		// partTreeSpecificationBuilder.appendStaticFilters(Parsers.parseSFilter(sFilter));
		partTreeSpecificationBuilder.setExpandPropertyPaths(Parsers.parseExpand(expand, partTreeSpecificationBuilder.getDomainClass(),
				pathWrapper.getProperty()));
		partTreeSpecificationBuilder.append(pageable);

		partTreeSpecificationBuilder.setParameterProcessor(parameterProcessor);
		QueryParameterHolder partTreeSpecification = partTreeSpecificationBuilder.build();
		String[] dtosArr = Parsers.parseDtos(dtos);
		if (pathWrapper.getProperty() == null) {
			return new ParsedRequest(partTreeSpecificationBuilder.getDomainClass(), partTreeSpecification, dtosArr);
		} else {
			return new ParsedRequest(partTreeSpecificationBuilder.getDomainClass(), PropertyPath.from(pathWrapper.getProperty(),
					partTreeSpecificationBuilder.getDomainClass()), partTreeSpecification, dtosArr);
		}
	}

	public void setParameterProcessor(ParameterProcessor parameterProcessor) {
		this.parameterProcessor = parameterProcessor;
	}
}
