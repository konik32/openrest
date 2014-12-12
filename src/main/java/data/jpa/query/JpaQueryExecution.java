package data.jpa.query;

import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.util.Assert;

import data.query.ParameterBinder;
import data.query.Parameters;
import data.query.ParametersParameterAccessor;


public abstract class JpaQueryExecution {


	/**
	 * Executes the given {@link AbstractStringBasedJpaQuery} with the given {@link ParameterBinder}.
	 * 
	 * @param query must not be {@literal null}.
	 * @param binder must not be {@literal null}.
	 * @return
	 */
	public Object execute(PartTreeJpaQuery query, Object[] values) {

		Assert.notNull(query);
		Assert.notNull(values);

		Object result;

		try {
			return result = doExecute(query, values);
		} catch (NoResultException e) {
			return null;
		}

	}

	/**
	 * Method to implement {@link AbstractStringBasedJpaQuery} executions by single enum values.
	 * 
	 * @param query
	 * @param binder
	 * @return
	 */
	protected abstract Object doExecute(PartTreeJpaQuery query, Object[] values);


	/**
	 * Executes the {@link AbstractStringBasedJpaQuery} to return a {@link org.springframework.data.domain.Page} of
	 * entities.
	 */
	public static class PagedExecution extends JpaQueryExecution {

		private final Parameters<?, ?> parameters;

		public PagedExecution(Parameters<?, ?> parameters) {

			this.parameters = parameters;
		}

		@Override
		@SuppressWarnings("unchecked")
		protected Object doExecute(PartTreeJpaQuery repositoryQuery, Object[] values) {

			// Execute query to compute total
			TypedQuery<Long> projection = repositoryQuery.createCountQuery(values);

			List<Long> totals = projection.getResultList();
			Long total = totals.size() == 1 ? totals.get(0) : totals.size();

			Query query = repositoryQuery.createQuery(values);
			ParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
			Pageable pageable = accessor.getPageable();

			List<Object> content = pageable == null || total > pageable.getOffset() ? query.getResultList() : Collections
					.emptyList();

			return new PageImpl<Object>(content, pageable, total);
		}
	}

	/**
	 * Executes a {@link AbstractStringBasedJpaQuery} to return a single entity.
	 */
	public static class SingleEntityExecution extends JpaQueryExecution {

		@Override
		protected Object doExecute(PartTreeJpaQuery query, Object[] values) {

			return query.createQuery(values).getSingleResult();
		}
	}

}
