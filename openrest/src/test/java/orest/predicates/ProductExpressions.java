package orest.predicates;

import orest.expression.registry.ExpressionMethod;
import orest.expression.registry.ExpressionRepository;
import orest.expression.registry.Join;
import orest.model.Product;
import orest.model.QProduct;
import orest.model.QTag;
import com.mysema.query.types.expr.BooleanExpression;

@ExpressionRepository(value = Product.class)
public class ProductExpressions {

	public BooleanExpression productionYearBetween(Integer from, Integer to) {
		return QProduct.product.productionYear.between(from, to);
	}
	
	
	@ExpressionMethod(searchMethod=true)
	public BooleanExpression userIdEq(Long userId){
		return QProduct.product.user.id.eq(userId);
	}
	
	@ExpressionMethod(joins=@Join(value="tags"))
	public BooleanExpression tagIdEq(Long tagId){
		return QTag.tag.id.eq(tagId);
	}
}
