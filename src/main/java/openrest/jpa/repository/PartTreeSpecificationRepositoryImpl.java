package openrest.jpa.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import open.rest.data.query.parser.OpenRestPartTree;
import openrest.domain.OpenRestQueryParameterHolder;
import openrest.jpa.query.OpenRestPartTreeJpaQuery;

import org.h2.index.PageDelegateIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import data.jpa.query.JpaQueryExecution.PagedExecution;
import data.jpa.query.JpaQueryExecution.SingleEntityExecution;
import data.jpa.query.OrderAndJoinQueryUtils;
import data.jpa.query.PartTreeJpaQuery;
import data.query.JpaParameters;
import data.query.parser.PartTree;

@Repository
public class PartTreeSpecificationRepositoryImpl implements PartTreeSpecificationRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Iterable<Object> findAll(OpenRestQueryParameterHolder spec, Class<Object> domainClass) {
		return (Iterable<Object>) new OpenRestPartTreeJpaQuery(em, domainClass, spec.getPartTree(), spec.getJpaParameters()).execute(spec.getValues(), new PagedExecution(spec.getJpaParameters()));
	}

	@Override
	public Object findOne(OpenRestQueryParameterHolder spec, Class<Object> domainClass) {
		return new OpenRestPartTreeJpaQuery(em, domainClass, spec.getPartTree(), spec.getJpaParameters()).execute(spec.getValues(), new SingleEntityExecution());
	}

//	private CrudMethodMetadata crudMethodMetadata;
//	
//
//	@Transactional
//	public Iterable<Object> findAll(OpenRestQueryParameterHolder spec, Class<Object> domainClass, Pageable pageable, Sort sort) {
//		if (pageable == null)
//			return getQuery(spec, sort, domainClass).getResultList();
//		else
//			return findAll(spec, pageable, domainClass);
//	}
//
//	@Transactional
//	private Page<Object> findAll(OpenRestQueryParameterHolder spec, Pageable pageable, Class<Object> domainClass) {
//		TypedQuery<Object> query = getQuery(spec, pageable, domainClass);
//		return pageable == null ? new PageImpl<Object>(query.getResultList()) : readPage(query, pageable, spec, domainClass);
//	}
//
//	@Transactional
//	private List<Object> findAll(OpenRestQueryParameterHolder spec, Sort sort, Class<Object> domainClass) {
//
//		return getQuery(spec, sort, domainClass).getResultList();
//	}
//
//	@Override
//	@Transactional
//	public Long getCount(OpenRestQueryParameterHolder spec, Class<Object> domainClass) {
//		return QueryUtils.executeCountQuery(getCountQuery(spec, domainClass));
//	}
//
//	/**
//	 * Reads the given {@link TypedQuery} into a {@link Page} applying the given
//	 * {@link Pageable} and {@link OpenRestQueryParameterHolder}.
//	 * 
//	 * @param query
//	 *            must not be {@literal null}.
//	 * @param spec
//	 *            can be {@literal null}.
//	 * @param pageable
//	 *            can be {@literal null}.
//	 * @return
//	 */
//
//	protected Page<Object> readPage(TypedQuery<Object> query, Pageable pageable, OpenRestQueryParameterHolder spec, Class<Object> domainClass) {
//		query.setFirstResult(pageable.getOffset());
//		query.setMaxResults(pageable.getPageSize());
//
//		Long total = QueryUtils.executeCountQuery(getCountQuery(spec, domainClass));
//		List<Object> content = total > pageable.getOffset() ? query.getResultList() : Collections.<Object> emptyList();
//
//		return new PageImpl<Object>(content, pageable, total);
//	}
//
//	/**
//	 * Creates a new {@link TypedQuery} from the given
//	 * {@link OpenRestQueryParameterHolder}.
//	 * 
//	 * @param spec
//	 *            can be {@literal null}.
//	 * @param pageable
//	 *            can be {@literal null}.
//	 * @return
//	 */
//	protected TypedQuery<Object> getQuery(OpenRestQueryParameterHolder spec, Pageable pageable, Class<Object> domainClass) {
//
//		Sort sort = pageable == null ? null : pageable.getSort();
//		return getQuery(spec, sort, domainClass);
//	}
//
//	/**
//	 * Creates a {@link TypedQuery} for the given {@link OpenRestQueryParameterHolder}
//	 * and {@link Sort}.
//	 * 
//	 * @param spec
//	 *            can be {@literal null}.
//	 * @param sort
//	 *            can be {@literal null}.
//	 * @return
//	 */
//	protected TypedQuery<Object> getQuery(OpenRestQueryParameterHolder spec, Sort sort, Class<Object> domainClass) {
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<Object> query = builder.createQuery(domainClass);
//
//		query.distinct(spec.isDistinct());
//		Root<Object> root = applyBoostSpecificationToCriteria(spec, query, domainClass, false);
//		Selection selection = root;
//		if(spec.isPropertyQuery())
//			selection = root.get(spec.getPropertyName());
//		query.select(selection);
////		List<Path> selectionsPaths = spec.getSelectionPath(root);
////		if (selectionsPaths == null)
////			query.select(root);
////		else {
////			if (selectionsPaths.size() > 1)
////				query.multiselect(selectionsPaths.toArray(new Path[] {}));
////			else
////				query.select(selectionsPaths.get(0));
////		}
//
//		if (sort != null) {
//			query.orderBy(OrderAndJoinQueryUtils.toOrders(sort, root, builder));
//		}
//		TypedQuery<Object> tq = applyRepositoryMethodMetadata(em.createQuery(query));
//		return spec.getBinder().bind(tq);
//	}
//
//	/**
//	 * Creates a new count query for the given {@link OpenRestQueryParameterHolder}.
//	 * 
//	 * @param spec
//	 *            can be {@literal null}.
//	 * @return
//	 */
//	protected TypedQuery<Long> getCountQuery(OpenRestQueryParameterHolder spec, Class<Object> domainClass) {
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<Long> query = builder.createQuery(Long.class);
//
//		Root<Object> root = applyBoostSpecificationToCriteria(spec, query, domainClass, true);
//		Expression<?> selection = root;
//		
//		if(spec.isPropertyQuery()){
//			Set<Join<Object,?>> joins = root.getJoins();
//			if(joins != null){
//				for(Join<Object,?> j: joins){
//					if(j.getAttribute().getName().equals(spec.getPropertyName())){
//						selection = j;
//						break;
//					}
//				}
//			}
//			if(selection.equals(root)){
//				selection = root.join(spec.getPropertyName());
//			}
//		}
////		List<Path> selectionsPaths = spec.getSelectionPath(root);
////		if (selectionsPaths != null) {
////			selection = selectionsPaths.get(0);
////		}
//
//		if (spec.isDistinct()) {
//			query.select(builder.countDistinct(selection));
//		} else {
//			query.select(builder.count(selection));
//		}
//		TypedQuery<Long> tq = em.createQuery(query);
//		return spec.getBinder().bind(tq);
//	}
//
//	/**
//	 * Applies the given {@link OpenRestQueryParameterHolder} to the given
//	 * {@link CriteriaQuery}.
//	 * 
//	 * @param spec
//	 *            can be {@literal null}.
//	 * @param query
//	 *            must not be {@literal null}.
//	 * @return
//	 */
//	private <S> Root<Object> applyBoostSpecificationToCriteria(OpenRestQueryParameterHolder spec, CriteriaQuery<S> query, Class<Object> domainClass,
//			boolean isCountQuery) {
//
//		Assert.notNull(query);
//		Root<Object> root = query.from(domainClass);
//		if (spec == null) {
//			return root;
//		}
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		if (!isCountQuery)
//			spec.expandRoot(root);
//		Predicate predicate = spec.toPredicate(root, query, builder);
//		if (predicate != null) {
//			query.where(predicate);
//		}
//
//		return root;
//	}
//
//	private TypedQuery<Object> applyRepositoryMethodMetadata(TypedQuery<Object> query) {
//
//		if (crudMethodMetadata == null) {
//			return query;
//		}
//
//		LockModeType type = crudMethodMetadata.getLockModeType();
//		TypedQuery<Object> toReturn = type == null ? query : query.setLockMode(type);
//
//		for (Entry<String, Object> hint : crudMethodMetadata.getQueryHints().entrySet()) {
//			query.setHint(hint.getKey(), hint.getValue());
//		}
//
//		return toReturn;
//	}
//
//	public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
//		this.crudMethodMetadata = crudMethodMetadata;
//	}

}
