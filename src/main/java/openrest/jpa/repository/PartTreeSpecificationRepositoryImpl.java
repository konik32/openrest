package openrest.jpa.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import openrest.domain.PartTreeSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import data.jpa.query.OrderAndJoinQueryUtils;

@Repository
public class PartTreeSpecificationRepositoryImpl implements PartTreeSpecificationRepository {

	@PersistenceContext
	private EntityManager em;

	private CrudMethodMetadata crudMethodMetadata;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaBoostSpecificationExecutor
	 * #findAll(org.springframework.data.jpa.domain.BoostSpecification)
	 */
	@Transactional
	public Iterable<Object> findAll(PartTreeSpecification spec,
			Class<Object> domainClass, Pageable pageable, Sort sort) {
		if (pageable == null)
			return getQuery(spec, sort, domainClass).getResultList();
		else
			return findAll(spec, pageable, domainClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaBoostSpecificationExecutor
	 * #findAll(org.springframework.data.jpa.domain.BoostSpecification,
	 * org.springframework.data.domain.Pageable)
	 */
	@Transactional
	private Page<Object> findAll(PartTreeSpecification spec, Pageable pageable,
			Class<Object> domainClass) {
		TypedQuery<Object> query = getQuery(spec, pageable, domainClass);
		return pageable == null ? new PageImpl<Object>(query.getResultList())
				: readPage(query, pageable, spec, domainClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaBoostSpecificationExecutor
	 * #findAll(org.springframework.data.jpa.domain.BoostSpecification,
	 * org.springframework.data.domain.Sort)
	 */
	@Transactional
	private List<Object> findAll(PartTreeSpecification spec, Sort sort,
			Class<Object> domainClass) {

		return getQuery(spec, sort, domainClass).getResultList();
	}

	/**
	 * Reads the given {@link TypedQuery} into a {@link Page} applying the given
	 * {@link Pageable} and {@link PartTreeSpecification}.
	 * 
	 * @param query
	 *            must not be {@literal null}.
	 * @param spec
	 *            can be {@literal null}.
	 * @param pageable
	 *            can be {@literal null}.
	 * @return
	 */

	protected Page<Object> readPage(TypedQuery<Object> query,
			Pageable pageable, PartTreeSpecification spec,
			Class<Object> domainClass) {
		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Long total = QueryUtils.executeCountQuery(getCountQuery(spec,
				domainClass));
		List<Object> content = total > pageable.getOffset() ? query
				.getResultList() : Collections.<Object> emptyList();

		return new PageImpl<Object>(content, pageable, total);
	}

	/**
	 * Creates a new {@link TypedQuery} from the given
	 * {@link PartTreeSpecification}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @param pageable
	 *            can be {@literal null}.
	 * @return
	 */
	protected TypedQuery<Object> getQuery(PartTreeSpecification spec,
			Pageable pageable, Class<Object> domainClass) {

		Sort sort = pageable == null ? null : pageable.getSort();
		return getQuery(spec, sort, domainClass);
	}

	/**
	 * Creates a {@link TypedQuery} for the given {@link PartTreeSpecification} and
	 * {@link Sort}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @param sort
	 *            can be {@literal null}.
	 * @return
	 */
	protected TypedQuery<Object> getQuery(PartTreeSpecification spec, Sort sort,
			Class<Object> domainClass) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery(domainClass);
		
		query.distinct(spec.isDistinct());

		Root<Object> root = applyBoostSpecificationToCriteria(spec, query,
				domainClass, false);

		
		query.select(root);

		if (sort != null) {
			query.orderBy(OrderAndJoinQueryUtils.toOrders(sort, root, builder));
		}
		TypedQuery<Object> tq = applyRepositoryMethodMetadata(em
				.createQuery(query));
		return spec.getBinder().bind(tq);
	}

	/**
	 * Creates a new count query for the given {@link PartTreeSpecification}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @return
	 */
	protected TypedQuery<Long> getCountQuery(PartTreeSpecification spec,
			Class<Object> domainClass) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<Object> root = applyBoostSpecificationToCriteria(spec, query,
				domainClass, true);

		if (spec.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}
		TypedQuery<Long> tq = em.createQuery(query);
		return spec.getBinder().bind(tq);
	}

	/**
	 * Applies the given {@link PartTreeSpecification} to the given
	 * {@link CriteriaQuery}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @param query
	 *            must not be {@literal null}.
	 * @return
	 */
	private <S> Root<Object> applyBoostSpecificationToCriteria(
			PartTreeSpecification spec, CriteriaQuery<S> query,
			Class<Object> domainClass, boolean isCountQuery) {

		Assert.notNull(query);
		Root<Object> root = query.from(domainClass);
		if (spec == null) {
			return root;
		}

		CriteriaBuilder builder = em.getCriteriaBuilder();
		if(!isCountQuery)
			spec.expandRoot(root);
		Predicate predicate = spec.toPredicate(root, query, builder);
		if (predicate != null) {
			query.where(predicate);
		}

		return root;
	}

	private TypedQuery<Object> applyRepositoryMethodMetadata(
			TypedQuery<Object> query) {

		if (crudMethodMetadata == null) {
			return query;
		}

		LockModeType type = crudMethodMetadata.getLockModeType();
		TypedQuery<Object> toReturn = type == null ? query : query
				.setLockMode(type);

		for (Entry<String, Object> hint : crudMethodMetadata.getQueryHints()
				.entrySet()) {
			query.setHint(hint.getKey(), hint.getValue());
		}

		return toReturn;
	}

	public void setRepositoryMethodMetadata(
			CrudMethodMetadata crudMethodMetadata) {
		this.crudMethodMetadata = crudMethodMetadata;
	}

}
