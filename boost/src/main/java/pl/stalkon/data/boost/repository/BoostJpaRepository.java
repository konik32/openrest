package pl.stalkon.data.boost.repository;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.LockMetadataProvider;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import pl.stalkon.data.boost.domain.BoostSpecification;

@Repository
public class BoostJpaRepository {

	@PersistenceContext
	private EntityManager em;

	private LockMetadataProvider lockMetadataProvider;

	@Transactional
	public <T> T findOne(BoostSpecification<T> spec, Class<T> domainClass) {
		try {
			return getQuery(spec, (Sort) null, domainClass).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaBoostSpecificationExecutor
	 * #findAll(org.springframework.data.jpa.domain.BoostSpecification)
	 */
	@Transactional
	public <T> Iterable<T> findAll(BoostSpecification<T> spec,
			Class<T> domainClass) {
		if (spec.getBinder().getPageable() == null)
			return getQuery(spec, spec.getBinder().getSort(), domainClass)
					.getResultList();
		else
			return findAll(spec, spec.getBinder().getPageable(), domainClass);
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
	private <T> Page<T> findAll(BoostSpecification<T> spec, Pageable pageable,
			Class<T> domainClass) {
		TypedQuery<T> query = getQuery(spec, pageable, domainClass);
		return pageable == null ? new PageImpl<T>(query.getResultList())
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
	private <T> List<T> findAll(BoostSpecification<T> spec, Sort sort,
			Class<T> domainClass) {

		return getQuery(spec, sort, domainClass).getResultList();
	}

	/**
	 * Reads the given {@link TypedQuery} into a {@link Page} applying the given
	 * {@link Pageable} and {@link BoostSpecification}.
	 * 
	 * @param query
	 *            must not be {@literal null}.
	 * @param spec
	 *            can be {@literal null}.
	 * @param pageable
	 *            can be {@literal null}.
	 * @return
	 */

	protected <T> Page<T> readPage(TypedQuery<T> query, Pageable pageable,
			BoostSpecification<T> spec, Class<T> domainClass) {

		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Long total = QueryUtils.executeCountQuery(getCountQuery(spec,
				domainClass));
		List<T> content = total > pageable.getOffset() ? query.getResultList()
				: Collections.<T> emptyList();

		return new PageImpl<T>(content, pageable, total);
	}

	/**
	 * Creates a new {@link TypedQuery} from the given
	 * {@link BoostSpecification}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @param pageable
	 *            can be {@literal null}.
	 * @return
	 */
	protected <T> TypedQuery<T> getQuery(BoostSpecification<T> spec,
			Pageable pageable, Class<T> domainClass) {
		
		Sort sort = pageable == null ? null : pageable.getSort();
		return getQuery(spec, sort, domainClass);
	}

	/**
	 * Creates a {@link TypedQuery} for the given {@link BoostSpecification} and
	 * {@link Sort}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @param sort
	 *            can be {@literal null}.
	 * @return
	 */
	protected <T> TypedQuery<T> getQuery(BoostSpecification<T> spec, Sort sort,
			Class<T> domainClass) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(domainClass);
		
		query.distinct(spec.isDistinct());
		
		Root<T> root = applyBoostSpecificationToCriteria(spec, query,
				domainClass);

		query.select(root);
		
		if (sort != null) {
			query.orderBy(toOrders(sort, root, builder));
		}
		TypedQuery<T> tq = applyLockMode(em.createQuery(query));
		return spec.getBinder().bind(tq);
	}

	/**
	 * Creates a new count query for the given {@link BoostSpecification}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @return
	 */
	protected <T> TypedQuery<Long> getCountQuery(BoostSpecification<T> spec,
			Class<T> domainClass) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<T> root = applyBoostSpecificationToCriteria(spec, query,
				domainClass);

		if (spec.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}
		TypedQuery<Long> tq = em.createQuery(query);
		return spec.getBinder().bind(tq);
	}

	/**
	 * Applies the given {@link BoostSpecification} to the given
	 * {@link CriteriaQuery}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @param query
	 *            must not be {@literal null}.
	 * @return
	 */
	private <T, S> Root<T> applyBoostSpecificationToCriteria(
			BoostSpecification<T> spec, CriteriaQuery<S> query,
			Class<T> domainClass) {

		Assert.notNull(query);
		Root<T> root = query.from(domainClass);
		if (spec == null) {
			return root;
		}

		CriteriaBuilder builder = em.getCriteriaBuilder();
		Predicate predicate = spec.toPredicate(root, query, builder);
		if (predicate != null) {
			query.where(predicate);
		}

		return root;
	}

	private <T> TypedQuery<T> applyLockMode(TypedQuery<T> query) {

		LockModeType type = lockMetadataProvider == null ? null
				: lockMetadataProvider.getLockModeType();
		return type == null ? query : query.setLockMode(type);
	}

	public void setLockMetadataProvider(
			LockMetadataProvider lockMetadataProvider) {
		this.lockMetadataProvider = lockMetadataProvider;
	}

}
