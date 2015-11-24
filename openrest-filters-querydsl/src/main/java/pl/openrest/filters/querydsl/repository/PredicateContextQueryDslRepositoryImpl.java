package pl.openrest.filters.querydsl.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import pl.openrest.filters.query.registry.JoinInformation;
import pl.openrest.filters.querydsl.query.QPredicateContext;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.AbstractJPAQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.CollectionPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

public class PredicateContextQueryDslRepositoryImpl<T, ID extends Serializable> extends QueryDslJpaRepository<T, ID> implements
        PredicateContextQueryDslRepository<T> {

    private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;
    private final EntityPath<T> path;
    private final PathBuilder<T> builder;
    private final Querydsl querydsl;
    // private final EntityManager em;
    private final PathBuilderFactory pathBuilderFactory = new PathBuilderFactory();

    public PredicateContextQueryDslRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        this(entityInformation, entityManager, DEFAULT_ENTITY_PATH_RESOLVER);
    }

    public PredicateContextQueryDslRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager,
            EntityPathResolver resolver) {

        super(entityInformation, entityManager, resolver);
        this.path = resolver.createPath(entityInformation.getJavaType());
        this.builder = new PathBuilder<T>(path.getType(), path.getMetadata());
        this.querydsl = new Querydsl(entityManager, builder);
    }

    @Override
    protected JPQLQuery createQuery(Predicate... predicate) {
        return super.createQuery(predicate);
    }

    @Override
    public T findOne(QPredicateContext predicateContext) {
        return createQuery(predicateContext).uniqueResult(path);
    }

    @Override
    public Iterable<T> findAll(QPredicateContext predicateContext) {
        return createQuery(predicateContext).list(path);
    }

    @Override
    public Iterable<T> findAll(QPredicateContext predicateContext, Sort sort) {
        JPQLQuery query = createQuery(predicateContext);
        query = querydsl.applySorting(sort, query);
        return query.list(path);
    }

    @Override
    public Page<T> findAll(QPredicateContext predicateContext, Pageable pageable) {
        JPQLQuery countQuery = createQuery(predicateContext);
        JPQLQuery query = querydsl.applyPagination(pageable, createQuery(predicateContext));

        Long total = countQuery.count();
        List<T> content = total > pageable.getOffset() ? query.list(path) : Collections.<T> emptyList();
        return new PageImpl<T>(content, pageable, total);
    }

    @Override
    public long count(QPredicateContext predicateContext) {
        return createQuery(predicateContext).count();
    }

    protected AbstractJPAQuery<JPAQuery> addJoins(AbstractJPAQuery<JPAQuery> query, QPredicateContext context) {
        for (JoinInformation join : context.getJoins()) {
            if (join.isCollection())
                query = query.leftJoin((CollectionPath) join.getPath(), pathBuilderFactory.create(join.getType()));
            else
                query = query.leftJoin((EntityPath) join.getPath());
            if (join.isFetch())
                query = query.fetch();
        }
        return query;
    }

    protected JPQLQuery createQuery(QPredicateContext context) {
        AbstractJPAQuery<JPAQuery> query = querydsl.createQuery(path);
        query = addJoins(query, context);
        query.where(context.getPredicate());

        CrudMethodMetadata metadata = getRepositoryMethodMetadata();

        if (metadata == null) {
            return query;
        }

        LockModeType type = metadata.getLockModeType();
        query = type == null ? query : query.setLockMode(type);

        for (Entry<String, Object> hint : getQueryHints().entrySet()) {
            query.setHint(hint.getKey(), hint.getValue());
        }

        return query;
    }
}
