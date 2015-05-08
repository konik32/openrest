package orest.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import orest.expression.registry.ExpressionMethodInformation.Join;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.Jpa21Utils;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.AbstractJPAQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.CollectionPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

public class PredicateContextQueryDslRepositoryImpl<T, ID extends Serializable> extends QueryDslJpaRepository<T, ID>
		implements PredicateContextQueryDslRepository<T> {

	private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;
	private final EntityPath<T> path;
	private final PathBuilder<T> builder;
	private final Querydsl querydsl;
	private final EntityManager em;
	private final PathBuilderFactory pathBuilderFactory = new PathBuilderFactory();

	public PredicateContextQueryDslRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
			EntityManager entityManager) {
		this(entityInformation, entityManager, DEFAULT_ENTITY_PATH_RESOLVER);
	}

	public PredicateContextQueryDslRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
			EntityManager entityManager, EntityPathResolver resolver) {
		super(entityInformation, entityManager);
		this.em = entityManager;
		this.path = resolver.createPath(entityInformation.getJavaType());
		this.builder = new PathBuilder<T>(path.getType(), path.getMetadata());
		this.querydsl = new Querydsl(entityManager, builder);
	}

	@Override
	protected JPQLQuery createQuery(Predicate... predicate) {
		return super.createQuery(predicate);
	}

	@Override
	public T findOne(Predicate predicate, PredicateContext predicateContext) {
		return createQuery(predicate, predicateContext).uniqueResult(path);
	}

	@Override
	public Iterable<T> findAll(Predicate predicate, PredicateContext predicateContext) {
		return createQuery(predicate, predicateContext).list(path);
	}

	@Override
	public Iterable<T> findAll(Predicate predicate, PredicateContext predicateContext, QSort sort) {
		JPQLQuery query = createQuery(predicate, predicateContext);
		query = querydsl.applySorting(sort, query);
		return query.list(path);
	}

	@Override
	public Page<T> findAll(Predicate predicate, PredicateContext predicateContext, Pageable pageable) {
		JPQLQuery countQuery = createQuery(predicate, predicateContext);
		JPQLQuery query = querydsl.applyPagination(pageable, createQuery(predicate, predicateContext));

		Long total = countQuery.count();
		List<T> content = total > pageable.getOffset() ? query.list(path) : Collections.<T> emptyList();
		return new PageImpl<T>(content, pageable, total);
	}

	@Override
	public long count(Predicate predicate, PredicateContext predicateContext) {
		return createQuery(predicate, predicateContext).count();
	}

	protected AbstractJPAQuery<JPAQuery> addJoins(AbstractJPAQuery<JPAQuery> query, PredicateContext context) {
		for (Join join : context.getJoins()) {
			if (join.isCollection())
				query = query.leftJoin((CollectionPath) join.getPath(), pathBuilderFactory.create(join.getType()));
			else
				query = query.leftJoin((EntityPath) join.getPath());
			if (join.isFetch())
				query = query.fetch();
		}
		return query;
	}

	protected JPQLQuery createQuery(Predicate predicate, PredicateContext context) {
		AbstractJPAQuery<JPAQuery> query = querydsl.createQuery(path);
		query = addJoins(query, context);
		query.where(predicate);

		CrudMethodMetadata metadata = getRepositoryMethodMetadata();

		if (metadata == null) {
			return query;
		}

		LockModeType type = metadata.getLockModeType();
		query = type == null ? query : query.setLockMode(type);

		for (Entry<String, Object> hint : metadata.getQueryHints().entrySet()) {
			query.setHint(hint.getKey(), hint.getValue());
		}

		JpaEntityGraph jpaEntityGraph = metadata.getEntityGraph();

		if (jpaEntityGraph == null) {
			return query;
		}

		EntityGraph<?> entityGraph = Jpa21Utils.tryGetFetchGraph(em, jpaEntityGraph);

		if (entityGraph == null) {
			return query;
		}

		query.setHint(jpaEntityGraph.getType().getKey(), entityGraph);

		return query;
	}
}
