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
import pl.stalkon.data.query.parser.PartTreeBuilder;

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

		PartTreeBuilder partTreeBuilder;

		if (pathParser.getProperty() != null) {
			Class<?> propertyType = PropertyPath.from(pathParser.getProperty(),
					domainClass).getType();
			partTreeBuilder = new PartTreeBuilder(propertyType, objectMapper);
			partTreeBuilder.appendParentIdPredicate(domainClass,
					pathParser.getProperty(), pathParser.getId());
		} else {
			partTreeBuilder = new PartTreeBuilder(domainClass, objectMapper);
			if (pathParser.getId() != null)
				partTreeBuilder.appendId(pathParser.getId());
		}

		if(filterParser.isParsed())
			partTreeBuilder.append(filterParser.getTempRoot(),
				filterParser.getParameters());

		PartTree partTree = partTreeBuilder.getPartTree();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		JpaParameters jpaParameters = new JpaParameters(
				partTreeBuilder.getJpaParameters(), -1, -1);
		Object values[] = partTreeBuilder.getParametersValues().toArray();

		ParameterBinder binder = new ParameterBinder(jpaParameters, values);

		ParametersParameterAccessor accessor = new ParametersParameterAccessor(
				jpaParameters, values);

		ParameterMetadataProvider provider = new ParameterMetadataProvider(
				builder, accessor);

		List<PropertyPath> viewPropertyPaths = new ViewsParser(views,
				partTreeBuilder.getDomainClass()).parse();
		if (pathParser.getProperty() == null) {
			return new ParsedRequest(partTreeBuilder.getDomainClass(),
					new PartTreeSpecification(partTree, provider, binder,
							viewPropertyPaths));
		} else {
			return new ParsedRequest(PropertyPath.from(
					pathParser.getProperty(), domainClass),
					new PartTreeSpecification(partTree, provider, binder,
							viewPropertyPaths));
		}
	}
}
