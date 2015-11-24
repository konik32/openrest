package pl.openrest.filters.querydsl.webmvc.support;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.querydsl.query.QPredicateContextBuilderFactory;
import pl.openrest.filters.querydsl.query.QPredicateContextBuilderFactory.QPredicateContextBuilder;
import pl.openrest.predicate.parser.PredicateParts;
import pl.openrest.predicate.parser.PredicatePartsExtractor;

import com.mysema.query.types.Expression;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;

@RequiredArgsConstructor
public class PageAndSortUtils {

    private final QPredicateContextBuilderFactory predicateContextBuilderFactory;
    private final PredicatePartsExtractor predicatePartsExtractor;

    public QPageRequest toQPageRequest(Pageable pageable, FilterableEntityInformation entityInfo) {
        QSort qSort = null;
        if (pageable.getSort() != null)
            qSort = toQSort(pageable.getSort(), entityInfo);
        return new QPageRequest(pageable.getPageNumber(), pageable.getPageSize(), qSort);

    }

    public QSort toQSort(Sort sort, FilterableEntityInformation entityInfo) {
        if (sort == null)
            return null;
        return new QSort(convertToOrderSpecifiers(sort, entityInfo));
    }

    private OrderSpecifier<?>[] convertToOrderSpecifiers(Sort sort, FilterableEntityInformation entityInfo) {
        List<OrderSpecifier> orders = new ArrayList<OrderSpecifier>();
        for (Sort.Order o : sort) {
            orders.add(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, getExpression(o, entityInfo)));
        }
        return orders.toArray(new OrderSpecifier[orders.size()]);
    }

    private Expression getExpression(Sort.Order order, FilterableEntityInformation entityInfo) {
        PredicateParts predicateParts = predicatePartsExtractor.extractParts(order.getProperty());
        if (entityInfo.getPredicateInformation(predicateParts.getPredicateName()) != null) {
            QPredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);
            predicateContextBuilder.withPredicateParts(predicateParts);
            return predicateContextBuilder.build().getPredicate();
        }
        return predicateContextBuilderFactory.getPathBuilderFactory().create(entityInfo.getEntityType()).get(order.getProperty());
    }
}
