package org.springframework.data.rest.webmvc;

import java.util.Iterator;

import lombok.Setter;
import orest.expression.ExpressionBuilder;
import orest.expression.registry.EntityExpressionMethodsRegistry;
import orest.expression.registry.ExpressionEntityInformation;
import orest.expression.registry.ExpressionMethodInformation;
import orest.expression.registry.ProjectionInfo;
import orest.expression.registry.ProjectionInfoRegistry;
import orest.parser.FilterPart;
import orest.parser.FilterStringParser;
import orest.repository.PredicateContext;
import orest.repository.QueryDslPredicateInvoker;
import orest.security.ExpressionEvaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysema.query.types.expr.BooleanExpression;

@RepositoryRestController
public class ExpressionController extends AbstractRepositoryRestController {

	private static final String BASE_MAPPING = "/{repository}";

	private final FilterStringParser filterStringParser;
	private final ExpressionBuilder expressionBuilder;
	private final ProjectionInfoRegistry projectionExpandsRegistry;

	private final EntityExpressionMethodsRegistry entityExpressionMethodsRegistry;

	@Autowired
	private @Setter ExpressionEvaluator expressionEvaluator;

	@Autowired
	public ExpressionController(PagedResourcesAssembler<Object> pagedResourcesAssembler,
			FilterStringParser filterStringParser, ExpressionBuilder expressionBuilder,
			EntityExpressionMethodsRegistry entityExpressionMethodsRegistry,
			ProjectionInfoRegistry projectionExpandsRegistry) {
		super(pagedResourcesAssembler);
		this.filterStringParser = filterStringParser;
		this.expressionBuilder = expressionBuilder;
		this.entityExpressionMethodsRegistry = entityExpressionMethodsRegistry;
		this.projectionExpandsRegistry = projectionExpandsRegistry;
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING + "/search/{search}", method = RequestMethod.GET, params = "orest")
	public ResponseEntity<Object> executeSearchWithFilters(RootResourceInformation rootResourceInformation,
			DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler,
			@PathVariable String search, @RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "expand", required = false) String expand,
			@RequestParam(value = "projection", required = false) String projection) {
		Object result = getResult(rootResourceInformation, assembler, pageable, sort, filters, expand, search, null,
				projection);
		return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = "orest")
	public ResponseEntity<Object> getCollectionWithFilters(RootResourceInformation rootResourceInformation,
			DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "expand", required = false) String expand,
			@RequestParam(value = "projection", required = false) String projection) {
		Object result = getResult(rootResourceInformation, assembler, pageable, sort, filters, expand, null, null,
				projection);
		return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.GET, params = "orest")
	public ResponseEntity<Object> getWithFilters(RootResourceInformation rootResourceInformation,
			DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler,
			@PathVariable("id") String id, @RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "expand", required = false) String expand,
			@RequestParam(value = "projection", required = false) String projection) {
		Iterable<Object> result = getResult(rootResourceInformation, assembler, pageable, sort, filters, expand, null,
				id, projection);
		Iterator<Object> it = result.iterator();
		if (!it.hasNext())
			throw new ResourceNotFoundException();
		return new ResponseEntity<Object>(assembler.toFullResource(it.next()), HttpStatus.OK);
	}

	private Iterable<Object> getResult(RootResourceInformation rootResourceInformation,
			PersistentEntityResourceAssembler assembler, DefaultedPageable pageable, Sort sort, String filters,
			String expand, String search, String id, String projection) {

		ExpressionEntityInformation expEntityInfo = entityExpressionMethodsRegistry
				.getEntityInformation(rootResourceInformation.getDomainType());
		ProjectionInfo projectionInfo = projectionExpandsRegistry.get(projection, expEntityInfo.getEntityType());
		authorizeProjection(projectionInfo);
		if (expEntityInfo == null)
			throw new ResourceNotFoundException();
		FilterPart filtersPartTree = filterStringParser.getFilterPart(filters, expEntityInfo);
		FilterPart searchMethodPart = filterStringParser.getSearchFilterPart(search, expEntityInfo);

		PredicateContext predicateContext = new PredicateContext();
		BooleanExpression searchMethodPredicate = null;
		if (searchMethodPart != null)
			searchMethodPredicate = expressionBuilder.create(searchMethodPart.getMethodInfo(), predicateContext,
					expEntityInfo, searchMethodPart.getParameters());
		BooleanExpression filtersPredicate = expressionBuilder.create(filtersPartTree, predicateContext,
				expEntityInfo.getExpressionRepository());
		BooleanExpression staticFiltersPredicate = expressionBuilder.createStaticFiltersExpression(predicateContext,
				expEntityInfo);
		BooleanExpression idPredicate = expressionBuilder.createIdEqualsExpression(id, expEntityInfo);

		expressionBuilder.addExpandJoins(predicateContext, expand, expEntityInfo.getEntityType());
		if (projectionInfo != null)
			predicateContext.addJoins(projectionInfo.getExpands());
		BooleanExpression finalPredicate = searchMethodPredicate == null ? null : searchMethodPredicate;

		finalPredicate = finalPredicate == null ? idPredicate : finalPredicate.and(idPredicate);
		finalPredicate = finalPredicate == null ? staticFiltersPredicate : finalPredicate.and(staticFiltersPredicate);
		finalPredicate = finalPredicate == null ? filtersPredicate : finalPredicate.and(filtersPredicate);
		Iterable<Object> result;

		boolean defaultedPageable = searchMethodPart != null ? isDefaultedPageable(expEntityInfo,
				searchMethodPart.getMethodInfo()) : isDefaultedPageable(expEntityInfo, null);
		result = getResult(expEntityInfo.getPredicateInvoker(), finalPredicate, predicateContext, pageable, sort,
				defaultedPageable);
		return result;
	}

	private Iterable<Object> getResult(QueryDslPredicateInvoker invoker, BooleanExpression finalExpression,
			PredicateContext predicateContext, DefaultedPageable pageable, Sort sort, boolean defaultedPageable) {
		Iterable<Object> result;
		if (pageable.getPageable() == null || (pageable.isDefault() && !defaultedPageable))
			result = invoker.invokeFindAll(finalExpression, predicateContext, sort);
		else
			result = invoker.invokeFindAll(finalExpression, predicateContext, pageable.getPageable());
		return result;
	}

	private boolean isDefaultedPageable(ExpressionEntityInformation expEntityInfo,
			ExpressionMethodInformation searchMethodInfo) {
		if (searchMethodInfo != null)
			return expEntityInfo.isDefaultedPageable() && searchMethodInfo.isDefaultedPageable();
		return expEntityInfo.isDefaultedPageable();
	}

	private void authorizeProjection(ProjectionInfo projectionInfo) {
		if (projectionInfo == null || projectionInfo.getAuthorizationCondition() == null)
			return;
		if (expressionEvaluator != null)
			if (!expressionEvaluator.checkCondition(projectionInfo.getAuthorizationCondition()))
				throw new AccessDeniedException("Not authorized");

	}

}
