# Documentation IN PROGRESS 

If you want to read documentation for the lastest release that was described in an [article](http://www.codeproject.com/Articles/1029761/OpenRest) go to https://github.com/konik32/openrest/tree/0.2.4.RELEASE

# OpenRest

OpenRest is an extension to Spring Data Rest. It is composed of two main parts: filtering resources with `Predicate`'s and DTO mechanism. In Spring Data Rest you could filter reosources with query methods, but you cannot combine those methods eg. in some cases you need to filter products by it price, in some by price and category and in others by category and shop. For each of those cases you would have to define separate query method. In OpenRest you can define each of those filters separately (priceGt, categoryIn, shopIdEq etc.) and combine them later. 

## @PredicateRepository

`@PredicateRepository` marks a class that holds set of `Predicate`'s. Annotation's parameters: 

- `value` - entity type
- `defaultedPageable` - if set to `false` resources will be returned without pagination regardless of `defaultedPageable` values in `ExpressionMethod`s

If `PredicateRepository` is defined for some entity its repository interface hase to extend `PredicateContextQueryDslRepository<T>`.

```
@PredicateRepository(User.class)
public class UserPredicates {

    @Predicate
  	public BooleanExpression usernameEq(String username){
  		return QUser.user.username.eq(username);
  	}
  	@Predicate
  	public BooleanExpression emailEq(String email){
  		return QUser.user.email.eq(email);
  	}
  	@Predicate
  	public BooleanExpression active(){
  		return QUser.user.active.eq(true);
  	}
}
```

## Predicate

`@Predicate` marks exported methods. Those predicates could be then concatenated in GET requests. There are four types of `Predicate`'s:

- FILTER - `Predicate`'s that return QueryDsl `BooleanExpression`. Those predicates could be referenced in `filter` parameter
- STATIC_FILTER - `Predicate`'s that return QueryDsl `BooleanExpression` added to every request
- SEARCH - `Predicate` that could be used like query methods (`/resource/search/search_expression_method`). Those methods should also return `BooleanExpression`.
- sort - `Predicate`'s that returns QueryDsl `NumberExpression`. Those methods could be referenced in sort parameter

`@Predicate` parameters:
- `name`
- `type` - predicate type (SEARCH | FILTER)
- `defaultedPageable` - if set to `false` resources will be returned without pagination. This parameter works only for SEARCH predicates
- `joins` - array of `Join`'s that need to be added to query when `Predicate` is used.

#### Join

`@Join` parameters:

- `value` - entity's association name that will be left joined with entity
- `fetch` - flag indicating whether association should be fetched

## StaticFilter

`@StaticFilter` marks predicates as static filters. Returned `BooleanExpression` will be added to every request for entity.

`@StaticFilter` parameters:

- `parameters` - array of SpEl Strings that after evaluation will be passed to expression method as its parameters
- `offOnCondition` - SpEL String that has to evaluate to boolean. If true static filter won't be added to query.

To make use of `offOnCondition` parameter you have to implement `StaticFilterConditionEvaluator` and add it to `PredicateContextBuilderFactory` with setter.

## GET requests

OpenRest GET request has its own syntax:

`GET /repository/search/search_predicate(param1;param2)?orest&filter=predicate(param1;);or;predicate;and;predicate...`

`GET /repository?orest&filters=predicate;or;predicate;and;predicate&filter=predicate;or;...`

`GET /repository?orest&count`

`GET /repository/search/search_predicate(param)?orest&count`

- `orest` parameter is non-optional
- if `Predicate` has any optional parameter it can be ommited eg. `priceBetween(1;)` `priceBetween(;2)`
- if `Predicate` does not have any parameters then parentheses should be ommitted
- if there is no `PredicateRepository` for entity OpenRest will return status code 404
- available logical operators:
  - `;or;`
  - `;and;`
- logical expressions cannot be enclosed in brackets

## DTO mechanism

OpenRest provides DTO mechanism that is similar to Projection mechanism in Spring Data Rest. Every DTO is an object created from JSON web request and then it is mapped to specified entity field by field in POST and PUT requests or by getters/setters in PATCH requests. Uknown, final and static fields are ignored. The idea behind DTO mechanism was to decrease the number of controllers' endpoints and create, update entities by using DTOs, events and services.

## @Dto

`@Dto`  marks DTO classes.

`@Dto` parameters:

- `entityType` - entity type which will be created from or merged with this dto
- `name` - name of this dto which should be passed in POST, PUT, PATCH query parameter named `dto`. When name is not specified dto won't be exported, and could be used only as nested object in other dto.
- `type` - the type of dto, whether it will be used for entity creation, merging or both.

## CreateMapper and UpdateMapper

By default entities are created or merged with dto by mapping all matching fields or getters/setters. To have more controll over the process you should create bean that implements CreateMapper or UpdateMapper interface.

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

`@ValidateExpression` marks DTO fields that will be validated with SpEL expression

`@ValidateExpression` parameters:

- `value` - holds SpEL string which will be evaluated with Spring Security Context, DTO object and entity(in PATCH requests) under `dto` and `entity` keywords. To validate referenced objects use `@Valid` annotation (objects under `dto` and `entity` keywords are updated while validator traverses object graph).   

```
@ValidateExpression("#{dto.password != null && entity.password != null? @passwordService.checkIfCorrectPassword(entity.password): true}")
private String password;
```

To use this annotation you should declare `DtoFieldExpressionValidator` bean.

## @Value

`@Value` annotations can be used with DTOs in the same way as in Projections

## DtoAuthorizationStrategy

You can define DTO authoriztion business logic by implementing `DtoAuthorizationStrategy` strategy interface and passing its type to `@AuthorizeDto` annotation `value` parameter. 

```
public interface IsAdmin extends DtoAuthorizationStrategy<LoggedUser, Object, Object>{
    public boolean isAuthorized(LoggedUser principal, Object dto, Object entity){
    	return principal.hasRole('ROLE_ADMIN');
    }
}

@Dto(entityType = Product.class, type = DtoType.CREATE, name = "productCreateDto")
@AuthorizeDto(IsAdmin.class)
public class ProductCreateDto {}

```

If `@AuthorizeDto` annotation does not meet your needs you can add your custom `DtoAuthorizationStrategy` to `DtoAuthorizationStrategyMappingHandler`,

## BeforeCreateMappingHandler and BeforeUpdateMappingHandler

BeforeCreateMappingHandler and BeforeUpdateMappingHandler are invoked before dto to entity mapping. They are useful when you need to process dto or entity before mapping. OpenRest uses these handlers for `@Value` fields evaluation, validation and authorization.

## Events

To default Spring Data Rest events OpenRest adds four new: `@HandleAfterCreateWithDto`, `@HandleAfterSaveWithDto`, `@HandleBeforeCreateWithDto`, `@HandleBeforeSaveWithDto`. The concept of the events is the same. Added ones just holds DTO object in context.

## POST, PUT, PATCH requests

`POST, PUT, PATCH /resource?dto=dtoName`

## Configuration

```
@SpringBootApplication

@EnableOpenRest
@EnableOpenRestFilters
@EnableOpenRestDto
@EnableOpenRestDtoSecurity
@EnableOpenRestDtoValidation

public class Application {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
```





