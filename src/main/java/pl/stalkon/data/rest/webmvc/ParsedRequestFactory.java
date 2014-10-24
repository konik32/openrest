package pl.stalkon.data.rest.webmvc;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyPath;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.stalkon.data.boost.domain.PartTreeSpecification;
import pl.stalkon.data.boost.httpquery.parser.FilterParser;
import pl.stalkon.data.boost.httpquery.parser.PathParser;
import pl.stalkon.data.boost.httpquery.parser.ViewsParser;
import pl.stalkon.data.jpa.query.ParameterMetadataProvider;
import pl.stalkon.data.query.JpaParameters;
import pl.stalkon.data.query.ParameterBinder;
import pl.stalkon.data.query.ParametersParameterAccessor;
import pl.stalkon.data.query.parser.PartTree;
import pl.stalkon.data.query.parser.PartTreeSpecificationParametersBuilder;

public class ParsedRequestFactory {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	public PartTreeSpecification getBoostSpecification(PartTree tree,
			JpaParameters jpaParameters, Object values[],
			List<PropertyPath> viewPropertyPaths) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		ParameterBinder binder = new ParameterBinder(jpaParameters, values);
		ParametersParameterAccessor accessor = new ParametersParameterAccessor(
				jpaParameters, values);
		ParameterMetadataProvider provider = new ParameterMetadataProvider(
				builder, accessor);
		return new PartTreeSpecification(tree, provider, binder,
				viewPropertyPaths);
	}

	public PartTreeSpecification getBoostSpecification(Predicate predicate,
			JpaParameters jpaParameters, Object values[]) {
		ParameterBinder binder = new ParameterBinder(jpaParameters, values);
		return new PartTreeSpecification(predicate, binder);
	}

	public ParsedRequest getSpecificationInformation(String filter,
			String views, String subject, String path, Class<?> domainClass) {

		FilterParser filterParser = new FilterParser(filter);
		filterParser.parse();

		PathParser pathParser = new PathParser(path);
		pathParser.parse();

		PartTreeSpecificationParametersBuilder specificationParametersBuilder;

		if (pathParser.getProperty() != null) {
			Class<?> propertyType = PropertyPath.from(pathParser.getProperty(),
					domainClass).getType();
			specificationParametersBuilder = new PartTreeSpecificationParametersBuilder(propertyType, objectMapper);
			specificationParametersBuilder.appendParentIdPredicate(domainClass,
					pathParser.getProperty(), pathParser.getId());
		} else {
			specificationParametersBuilder = new PartTreeSpecificationParametersBuilder(domainClass, objectMapper);
			if (pathParser.getId() != null)
				specificationParametersBuilder.appendId(pathParser.getId());
		}

		if (filterParser.isParsed())
			specificationParametersBuilder.append(filterParser.getTempRoot(),
					filterParser.getParameters());

		PartTree partTree = specificationParametersBuilder.getPartTree();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		JpaParameters jpaParameters = new JpaParameters(
				specificationParametersBuilder.getJpaParameters(), -1, -1);
		
		Object values[] = specificationParametersBuilder.getParametersValues().toArray();

		List<PropertyPath> viewPropertyPaths = new ViewsParser(views,
				specificationParametersBuilder.getDomainClass()).parse();
		
		PartTreeSpecification partTreeSpecification = new PartTreeSpecification(
				partTree, jpaParameters, values, builder, viewPropertyPaths);
		
		if (pathParser.getProperty() == null) {
			return new ParsedRequest(specificationParametersBuilder.getDomainClass(),
					partTreeSpecification);
		} else {
			return new ParsedRequest(PropertyPath.from(
					pathParser.getProperty(), domainClass),
					partTreeSpecification);
		}
	}
}
