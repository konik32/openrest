package orest.webmvc.support;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import orest.expression.ExpressionBuilder;
import orest.expression.registry.ExpressionEntityInformation;
import orest.expression.registry.ExpressionMethodInformation;
import orest.parser.FilterPart;
import orest.parser.FilterStringParser;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;

import com.mysema.query.types.Expression;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

@RequiredArgsConstructor
public class PageAndSortUtils {

	private final ExpressionBuilder expressionBuilder;
	private final FilterStringParser filterStringParser;
	private final PathBuilderFactory factory = new PathBuilderFactory();

	public QPageRequest toQPageRequest(Pageable pageable, ExpressionEntityInformation entityInfo) {
		QSort qSort = null;
		if (pageable.getSort() != null)
			qSort = toQSort(pageable.getSort(), entityInfo);
		return new QPageRequest(pageable.getPageNumber(), pageable.getPageSize(), qSort);

	}

	public QSort toQSort(Sort sort, ExpressionEntityInformation entityInfo) {
		if (sort == null)
			return null;
		return new QSort(convertToOrderSpecifiers(sort, entityInfo));
	}

	private OrderSpecifier<?>[] convertToOrderSpecifiers(Sort sort, ExpressionEntityInformation entityInfo) {
		List<OrderSpecifier> orders = new ArrayList<OrderSpecifier>();
		for (Sort.Order o : sort) {
			orders.add(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, getExpression(o, entityInfo)));
		}
		return orders.toArray(new OrderSpecifier[orders.size()]);
	}

	private Expression getExpression(Sort.Order order, ExpressionEntityInformation entityInfo) {
		FilterPart filterPart = filterStringParser.getMethodFilterPart(order.getProperty(), entityInfo);
		ExpressionMethodInformation expMethodInfo = filterPart.getMethodInfo();
		if (expMethodInfo != null) {
			return expressionBuilder.create(filterPart.getMethodInfo(), null, entityInfo, filterPart.getParameters());
		}
		return factory.create(entityInfo.getEntityType()).get(order.getProperty());
	}
}
