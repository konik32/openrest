package pl.stalkon.data.rest.webmvc;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.context.PersistentEntities;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.stalkon.data.boost.domain.PartTreeSpecification;
import pl.stalkon.data.boost.httpquery.parser.FilterParser;
import pl.stalkon.data.boost.httpquery.parser.FilterParser.PathWrapper;
import pl.stalkon.data.boost.query.StaticFilterFactory;
import pl.stalkon.data.jpa.query.ParameterMetadataProvider;
import pl.stalkon.data.query.JpaParameters;
import pl.stalkon.data.query.ParameterBinder;
import pl.stalkon.data.query.ParametersParameterAccessor;
import pl.stalkon.data.query.parser.PartTree;
import pl.stalkon.data.query.parser.PartTreeSpecificationBuilder;

public class ParsedRequestFactory {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PersistentEntities persistentEntities;

	@Autowired
	private StaticFilterFactory staticaStaticFilterFactory;

	public PartTreeSpecification getBoostSpecification(PartTree tree, JpaParameters jpaParameters, Object values[], List<PropertyPath> viewPropertyPaths) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		ParameterBinder binder = new ParameterBinder(jpaParameters, values);
		ParametersParameterAccessor accessor = new ParametersParameterAccessor(jpaParameters, values);
		ParameterMetadataProvider provider = new ParameterMetadataProvider(builder, accessor);
		return new PartTreeSpecification(tree, provider, binder, viewPropertyPaths);
	}

	public PartTreeSpecification getBoostSpecification(Predicate predicate, JpaParameters jpaParameters, Object values[]) {
		ParameterBinder binder = new ParameterBinder(jpaParameters, values);
		return new PartTreeSpecification(predicate, binder);
	}

	public ParsedRequest getSpecificationInformation(String filter, String expand, String subject, String path, String sFilter, Class<?> domainClass) {

		PathWrapper pathWrapper = FilterParser.parsePath(path);

		PartTreeSpecificationBuilder specificationParametersBuilder;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		if (pathWrapper.getProperty() != null) {
			Class<?> propertyType = PropertyPath.from(pathWrapper.getProperty(), domainClass).getType();
			specificationParametersBuilder = new PartTreeSpecificationBuilder(persistentEntities.getPersistentEntity(propertyType), objectMapper, builder,
					staticaStaticFilterFactory);
			specificationParametersBuilder.append(persistentEntities.getPersistentEntity(domainClass), pathWrapper.getProperty(), pathWrapper.getId());
		} else {
			specificationParametersBuilder = new PartTreeSpecificationBuilder(persistentEntities.getPersistentEntity(domainClass), objectMapper, builder,
					staticaStaticFilterFactory);
			if (pathWrapper.getId() != null)
				specificationParametersBuilder.append(pathWrapper.getId());
		}

		specificationParametersBuilder.append(FilterParser.parseFilter(filter));

		specificationParametersBuilder.appendStaticFilters(FilterParser.parseSFilter(sFilter));

		specificationParametersBuilder.setExpandPropertyPaths(FilterParser.parseExpand(expand, specificationParametersBuilder.getDomainClass()));

		PartTreeSpecification partTreeSpecification = specificationParametersBuilder.build();

		if (pathWrapper.getProperty() == null) {
			return new ParsedRequest(specificationParametersBuilder.getDomainClass(), partTreeSpecification);
		} else {
			return new ParsedRequest(PropertyPath.from(pathWrapper.getProperty(), domainClass), partTreeSpecification);
		}
	}
}
