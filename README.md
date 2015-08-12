# Documentation IN PROGRESS 

# OpenRest

OpenRest is an extension to Spring Data Rest. It is composed of two main parts: filtering resources with `ExpressionMethod`'s and DTO mechanism. In Spring Data Rest you could filter reosources with query methods, but you cannot combine those methods eg. in some cases you need to filter products by it price, in some by price and category and in others by category and shop. For each of those cases you would have to define separate query method. In OpenRest you can define each of those filters separately (priceGt, categoryIn, shopIdEq etc.) and combine them later. 

## @ExpressionRepository

`@ExpressionRepository` is an annotation that marks a class which holds set of `ExpressionMethod`'s. It takes two parameters 

- `value` - entity type
- `defaultedPageable` - if set to `true` resources will be returned without pagination

If `EpressionRepository` is defined for some entity its repository interface hase to extend `PredicateContextQueryDslRepository<T>`.

```
@ExpressionRepository(User.class)
public class UserExpressions {

    @ExpressionMethod
  	public BooleanExpression usernameEq(String username){
  		return QUser.user.username.eq(username);
  	}
  	@ExpressionMethod
  	public BooleanExpression emailEq(String email){
  		return QUser.user.email.eq(email);
  	}
  	@ExpressionMethod
  	public BooleanExpression active(){
  		return QUser.user.active.eq(true);
  	}
}
```

## ExpressionMethod

`@ExpressionMethod` is an annotation which is used to mark methods that could be referenced in GET requests. There are three types of `ExpressionMethod`'s:

- filter - `ExpressionMethod`'s that return QueryDsl `BooleanExpression`. Those methods could be referenced in `filter` parameter
- search - `ExpressionMethod` that could be used like query methods (`/resource/search/search_expression_method`). Those methods should also return `BooleanExpression`.
- sort - `ExpressionMethod`'s that returns QueryDsl `NumberExpression`. Those methods could be referenced in sort parameter

`@ExpressionMethod` parameters:
- `exported` - flag which indicates whether a method could be referenced in GET request
- `name`
- `searchMethod` - flag to mark method as search method
- `defaultedPageable` - if set to `true` resources will be returned without pagination. This parameter can be used only when search param is set to true
- `joins` - array of `Join`'s that need to be added to query when `ExpressionMethod` is used.

#### Join

`@Join` parameters:

- `value` - entity's association name that will be left joined with entity
- `fetch` - flag indicating whether association should be fetched

## StaticFilter

`@StaticFilter` is an annotation to mark expression method as static filter. Returned `BooleanExpression` will be added to every request for entity.

`@StaticFilter` parameters:

- `parameters` - array of SpEl Strings that after evaluation will be passed to expression method as its parameters
- `offOnCondition` - SpEL String that has to evaluate to boolean. If true static filter won't be added to query. 

## Expand

Associations specified in `@Expand` value parameter will be fetched with projected entity. This annotation could only by added to classes annotated with Spring Data Rest Projection. Resources could also be expanded by adding `expand` parameter to query.

## Secure

`@Secure` annotation could be added to classes marked with `@Projection` and `@Dto`. SpEL expression specified in annotation value is evaluated with Spring Security Context. If expression evaluate to false, Access Denied exception is thrown.

## GET requests

OpenRest GET request has its own syntax:

`GET /resource/search/expression_search_method(param1;param2)?orest&filters=expression_method(param1;);or;expression_method;and;expression_method...`

`GET /resource?orest&filters=expression_method;or;expression_method;and;expression_method...`

- `orest` parameter is non-optional
- if `ExpressionMethod` has any optional parameter it can be ommited eg. `priceBetween(1;)` `priceBetween(;2)`
- if `ExpressionMethod` does not have any parameters then parentheses should be ommitted
- if there is no `ExpressionRepository` for entity OpenRest will return status code 404
- available logical operators:
  - `;or;`
  - `;and;`
- logical expressions cannot be enclosed in brackets

## DTO mechanism

OpenRest provides DTO mechanism that is very similar to Projection mechanism in Spring Data Rest. Every DTO is an object created from JSON web request and then it is mapped to specified entity field by field in POST and PUT requests or by getters/setters in PATCH requests. Uknown fields are ignored. The idea behind DTO mechanism was to decrease the number of controllers' endpoints and create, update entities by using DTOs, events and services.

## @Dto

`@Dto` is an annotation which is used to mark DTO class.

`@Dto` parameters:

- `entityType` - entity type which will be created from or merged with this dto
- `name` - name of this dto which should be passed in POST, PUT, PATCH query parameter named `dto`. When name is not specified dto won't be exported, and could be used only as nested object in other dto.
- `entityCreatorType`, `entityMergerType` - by default entities are created or merged with dto by mapping all matching fields or getters/setters. To have more controll over the process one should create custom bean and pass it by type
- `type` - the type of dto, whether it will be used for entity creation, merging or both.

## @Nullable

If a dto is used with PATCH requests, there is no way to tell if its field was deliberately set to null or wasn't initialized (no such field in JSON request). Such field should be mark with `@Nullable`

`@Nullable` parameters:

- `value` - Name of a boolean flag field which holds information whether this field was set to null or not initialized eg.
```
@Nullable("nameSet")
private String name;
private boolean nameSet = false;
 
public void setName(String name){
    this.name = name;
    this.nameSet = true; 
}
```

## @ValidateExpression

`@ValidateExpression` is an annotation to mark DTO fields that will be validated with SpEL expression

`@ValidateExpression` parameters:

- `value` - holds SpEL string which will be evaluated with Spring Security Context, DTO object and entity (in PUT, PATCH requests) eg.

```
@ValidateExpression("#{dto.password != null && entity.password != null? @passwordService.checkIfCorrectPassword(entity.password): true}")
```

## @Value

`@Value` annotations can be used with DTOs in the same way as in Projections

## Events

To default Spring Data Rest events ORest adds four new: `@HandleAfterCreateWithDto`, `@HandleAfterSaveWithDto`, `@HandleBeforeCreateWithDto`, `@HandleBeforeSaveWithDto`. The concept of the events is the same. Added ones just holds DTO object in context.

## POST, PUT, PATCH requests

`POST, PUT, PATCH /resource?dto=dtoName`

## Configuration

```
@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = ExpressionJpaFactoryBean.class)
@Import(ORestConfig.class)
public class Application {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
```





