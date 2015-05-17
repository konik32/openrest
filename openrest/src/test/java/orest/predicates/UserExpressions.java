package orest.predicates;

import orest.expression.registry.ExpressionMethod;
import orest.expression.registry.ExpressionRepository;
import orest.expression.registry.StaticFilter;
import orest.model.QUser;
import orest.model.User;

import com.mysema.query.types.expr.BooleanExpression;

@ExpressionRepository(User.class)
public class UserExpressions {

	@ExpressionMethod(exported=true, searchMethod=true)
	public BooleanExpression nameEq(String name){
		return QUser.user.name.eq(name);
	}
	
	public BooleanExpression surnameEq(String surname){
		return QUser.user.name.eq(surname);
	}
	
	@StaticFilter
	public BooleanExpression active(){
		return QUser.user.active.eq(true);
	}
}
