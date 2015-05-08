package orest.repository;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
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
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

@Data
public class QueryDslPredicateInvoker {
	private final PredicateContextQueryDslRepository<Object> repository;
	private final PathBuilder<?> builder;

	public Iterable<Object> invokeFindAll(Predicate predicate, PredicateContext predicateContext, Pageable pageable) {
		return pageable == null ? invokeFindAll(predicate, predicateContext) : repository.findAll(predicate,
				predicateContext, pageable);
	}

	public Iterable<Object> invokeFindAll(Predicate predicate, PredicateContext predicateContext, QSort sort) {
		if (sort == null)
			return invokeFindAll(predicate, predicateContext);
		return repository.findAll(predicate, predicateContext, sort);
	}

	public Iterable<Object> invokeFindAll(Predicate predicate, PredicateContext predicateContext) {
		return repository.findAll(predicate, predicateContext);
	}

}
