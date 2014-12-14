package data.jpa.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;


import org.springframework.data.domain.Sort;
import  data.jpa.query.JpaQueryExecution;

import data.jpa.query.JpaQueryExecution.PagedExecution;
import data.jpa.query.JpaQueryExecution.SingleEntityExecution;
import data.jpa.query.ParameterMetadataProvider.ParameterMetadata;
import data.query.CriteriaQueryParameterBinder;
import data.query.JpaParameters;
import data.query.ParameterBinder;
import data.query.ParametersParameterAccessor;
import data.query.parser.PartTree;


public class PartTreeJpaQuery {

	protected final Class<?> domainClass;
	protected final PartTree tree;
	protected final JpaParameters parameters;

	protected QueryPreparer query;
	protected QueryPreparer countQuery;
	protected final EntityManager em;

	/**
	 * Creates a new {@link PartTreeJpaQuery}.
	 * 
	 * @param em must not be {@literal null}.
	 */
	public PartTreeJpaQuery(EntityManager em, Class<?> domainClass, PartTree tree, JpaParameters parameters) {
		this.em = em;

		this.domainClass = domainClass;
		this.tree = tree;
		this.parameters = parameters;

		this.countQuery = new CountQueryPreparer(parameters.potentiallySortsDynamically());
		this.query = tree.isCountProjection() ? countQuery : new QueryPreparer(parameters.potentiallySortsDynamically());
	}
	
	public Object execute(Object[] values, JpaQueryExecution execution) {
		return execution.execute(this, values);
	}


	protected Query createQuery(Object[] values) {
		return doCreateQuery(values);
	}
	
	protected TypedQuery<Long> createCountQuery(Object[] values) {
		return doCreateCountQuery(values);
	}

	protected Query doCreateQuery(Object[] values) {
		return query.createQuery(values);
	}

	@SuppressWarnings("unchecked")
	protected TypedQuery<Long> doCreateCountQuery(Object[] values) {
		return (TypedQuery<Long>) countQuery.createQuery(values);
	}

	/**
	 * Query preparer to create {@link CriteriaQuery} instances and potentially cache them.
	 * 
	 * @author Oliver Gierke
	 */
	protected class QueryPreparer {

		private final CriteriaQuery<?> cachedCriteriaQuery;
		private final List<ParameterMetadata<?>> expressions;

		public QueryPreparer(boolean recreateQueries) {

			JpaQueryCreator creator = createCreator(null);
			this.cachedCriteriaQuery = recreateQueries ? null : creator.createQuery();
			this.expressions = recreateQueries ? null : creator.getParameterExpressions();
		}

		/**
		 * Creates a new {@link Query} for the given parameter values.
		 * 
		 * @param values
		 * @return
		 */
		public Query createQuery(Object[] values) {

			CriteriaQuery<?> criteriaQuery = cachedCriteriaQuery;
			List<ParameterMetadata<?>> expressions = this.expressions;
			ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);

			if (cachedCriteriaQuery == null || accessor.hasBindableNullValue()) {
				JpaQueryCreator creator = createCreator(accessor);
				criteriaQuery = creator.createQuery(getDynamicSort(values));
				expressions = creator.getParameterExpressions();
			}

			TypedQuery<?> jpaQuery = createQuery(criteriaQuery);

			return invokeBinding(getBinder(values, expressions), jpaQuery);
		}


		/**
		 * Checks whether we are working with a cached {@link CriteriaQuery} and snychronizes the creation of a
		 * {@link TypedQuery} instance from it. This is due to non-thread-safety in the {@link CriteriaQuery} implementation
		 * of some persistence providers (i.e. Hibernate in this case).
		 * 
		 * @see DATAJPA-396
		 * @param criteriaQuery must not be {@literal null}.
		 * @return
		 */
		private TypedQuery<?> createQuery(CriteriaQuery<?> criteriaQuery) {

			if (this.cachedCriteriaQuery != null) {
				synchronized (this.cachedCriteriaQuery) {
					return em.createQuery(criteriaQuery);
				}
			}

			return em.createQuery(criteriaQuery);
		}

		protected JpaQueryCreator createCreator(ParametersParameterAccessor accessor) {

			EntityManager entityManager = em;
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();

			ParameterMetadataProvider provider = accessor == null ? new ParameterMetadataProvider(builder, parameters)
					: new ParameterMetadataProvider(builder, accessor);

			return new JpaQueryCreator(tree, domainClass, builder, provider);
		}

		/**
		 * Invokes parameter binding on the given {@link TypedQuery}.
		 * 
		 * @param binder
		 * @param query
		 * @return
		 */
		protected Query invokeBinding(ParameterBinder binder, TypedQuery<?> query) {

			return binder.bindAndPrepare(query);
		}

		private ParameterBinder getBinder(Object[] values, List<ParameterMetadata<?>> expressions) {
			return new CriteriaQueryParameterBinder(parameters, values, expressions);
		}

		private Sort getDynamicSort(Object[] values) {

			return parameters.potentiallySortsDynamically() ? new ParametersParameterAccessor(parameters, values).getSort()
					: null;
		}
	}

	/**
	 * Special {@link QueryPreparer} to create count queries.
	 * 
	 * @author Oliver Gierke
	 */
	protected class CountQueryPreparer extends QueryPreparer {

		public CountQueryPreparer(boolean recreateQueries) {
			super(recreateQueries);
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.data.jpa.repository.query.PartTreeJpaQuery.QueryPreparer#createCreator(org.springframework.data.repository.query.ParametersParameterAccessor)
		 */
		@Override
		protected JpaQueryCreator createCreator(ParametersParameterAccessor accessor) {

			EntityManager entityManager = em;
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();

			ParameterMetadataProvider provider = accessor == null ? new ParameterMetadataProvider(builder, parameters)
					: new ParameterMetadataProvider(builder, accessor);

			return new JpaCountQueryCreator(tree, domainClass, builder, provider);
		}

		/**
		 * Customizes binding by skipping the pagination.
		 * 
		 * @see org.springframework.data.jpa.repository.query.PartTreeJpaQuery.QueryPreparer#invokeBinding(org.springframework.data.jpa.repository.query.ParameterBinder,
		 *      javax.persistence.TypedQuery)
		 */
		@Override
		protected Query invokeBinding(ParameterBinder binder, javax.persistence.TypedQuery<?> query) {
			return binder.bind(query);
		}
	}
}
