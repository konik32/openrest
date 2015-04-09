package orest.repository;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

@Data
public class QueryDslPredicateInvoker {

	private final PredicateContextQueryDslRepository<Object> repository;
	private final PathBuilder builder;

	public Iterable<Object> invokeFindAll(Predicate predicate,PredicateContext predicateContext, Pageable pageable) {
		return pageable == null ? invokeFindAll(predicate,predicateContext) : repository
				.findAll(predicate,predicateContext, pageable);
	}

	public Iterable<Object> invokeFindAll(Predicate predicate,PredicateContext predicateContext, Sort sort) {
		if (sort == null)
			return invokeFindAll(predicate,predicateContext);
		return repository.findAll(predicate,predicateContext, convertToOrderSpecifiers(sort));
	}

	public Iterable<Object> invokeFindAll(Predicate predicate,PredicateContext predicateContext) {
		return repository.findAll(predicate,predicateContext);
	}

	private OrderSpecifier<?>[] convertToOrderSpecifiers(Sort sort) {
		List<OrderSpecifier> orders = new ArrayList<OrderSpecifier>();
		for (Sort.Order o : sort) {
			orders.add(new OrderSpecifier(o.isAscending() ? Order.ASC
					: Order.DESC, builder.get(o.getProperty())));
		}
		return orders.toArray(new OrderSpecifier[orders.size()]);
	}

}
