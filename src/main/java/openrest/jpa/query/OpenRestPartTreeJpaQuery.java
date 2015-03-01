package openrest.jpa.query;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

import openrest.data.query.parser.OpenRestPartTree;
import data.jpa.query.JpaQueryCreator;
import data.jpa.query.ParameterMetadataProvider;
import data.jpa.query.PartTreeJpaQuery;
import data.query.JpaParameters;
import data.query.ParametersParameterAccessor;

public class OpenRestPartTreeJpaQuery extends PartTreeJpaQuery {

	private OpenRestPartTree tree;

	public OpenRestPartTreeJpaQuery(EntityManager em, Class<?> domainClass, OpenRestPartTree tree, JpaParameters parameters) {
		super(em, domainClass, tree, parameters);
		this.tree = tree;
		this.countQuery = new OpenRestCountQueryPreparer(parameters.potentiallySortsDynamically());
		this.query = tree.isCountProjection() ? countQuery : new OpenRestQueryPreparer(parameters.potentiallySortsDynamically());
	}

	private class OpenRestQueryPreparer extends QueryPreparer {

		public OpenRestQueryPreparer(boolean recreateQueries) {
			super(recreateQueries);
		}

		@Override
		protected JpaQueryCreator createCreator(ParametersParameterAccessor accessor) {
			EntityManager entityManager = em;
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();

			ParameterMetadataProvider provider = accessor == null ? new ParameterMetadataProvider(builder, parameters) : new ParameterMetadataProvider(builder,
					accessor);
			return new OpenRestJpaQueryCreator(tree, domainClass, builder, provider);
		}

	}

	private class OpenRestCountQueryPreparer extends CountQueryPreparer {
		public OpenRestCountQueryPreparer(boolean recreateQueries) {
			super(recreateQueries);
		}

		@Override
		protected JpaQueryCreator createCreator(ParametersParameterAccessor accessor) {
			EntityManager entityManager = em;
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();

			ParameterMetadataProvider provider = accessor == null ? new ParameterMetadataProvider(builder, parameters) : new ParameterMetadataProvider(builder,
					accessor);
			return new OpenRestJpaCountQueryCreator(tree, domainClass, builder, provider);
		}

	}

}
