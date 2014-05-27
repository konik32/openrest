package pl.stalkon.data.boost.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import org.springframework.data.jpa.query.ParameterMetadataProvider;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.query.JpaParameters;
import org.springframework.data.query.ParameterBinder;
import org.springframework.data.query.ParametersParameterAccessor;
import org.springframework.data.query.parser.PartTree;

public class PartTreeSpecificationFactory {

	@PersistenceContext
	private  EntityManager em;

	public PartTreeSpecification getBoostSpecification(
			PartTree tree, JpaParameters jpaParameters, Object values[],
			List<PropertyPath> viewPropertyPaths) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		ParameterBinder binder = new ParameterBinder(jpaParameters, values);
		ParametersParameterAccessor accessor = new ParametersParameterAccessor(
				jpaParameters, values);
		ParameterMetadataProvider provider = new ParameterMetadataProvider(
				builder, accessor);
		return new PartTreeSpecification (tree, provider, binder, viewPropertyPaths);
	}

	public PartTreeSpecification getBoostSpecification(
			Predicate predicate, JpaParameters jpaParameters, Object values[]) {
		ParameterBinder binder = new ParameterBinder(jpaParameters, values);
		return new PartTreeSpecification(predicate, binder);
	}
}
 