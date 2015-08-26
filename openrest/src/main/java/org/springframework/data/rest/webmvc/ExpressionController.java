package org.springframework.data.rest.webmvc;

import lombok.Setter;
import orest.expression.ExpressionBuilder;
import orest.expression.RequestBooleanExpressionBuilder;
import orest.expression.registry.EntityExpressionMethodsRegistry;
import orest.expression.registry.ExpressionEntityInformation;
import orest.expression.registry.ExpressionMethodInformation;
import orest.expression.registry.ProjectionInfo;
import orest.expression.registry.ProjectionInfoRegistry;
import orest.mvc.CountResponse;
import orest.parser.FilterPart;
import orest.parser.FilterStringParser;
import orest.projection.authorization.ProjectionAuthorizationStrategy;
import orest.repository.QueryDslPredicateInvoker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class ExpressionController extends AbstractRepositoryRestController {

    private static final String BASE_MAPPING = "/{repository}";

    private final FilterStringParser filterStringParser;
    private final ExpressionBuilder expressionBuilder;
    private final ProjectionInfoRegistry projectionExpandsRegistry;

    private final EntityExpressionMethodsRegistry entityExpressionMethodsRegistry;

    @Autowired(required = false)
    private @Setter ProjectionAuthorizationStrategy projectionAuthorizationStrategy;
    
    @Autowired
    public ExpressionController(PagedResourcesAssembler<Object> pagedResourcesAssembler, FilterStringParser filterStringParser,
            ExpressionBuilder expressionBuilder, EntityExpressionMethodsRegistry entityExpressionMethodsRegistry,
            ProjectionInfoRegistry projectionExpandsRegistry) {
        super(pagedResourcesAssembler);
        this.filterStringParser = filterStringParser;
        this.expressionBuilder = expressionBuilder;
        this.entityExpressionMethodsRegistry = entityExpressionMethodsRegistry;
        this.projectionExpandsRegistry = projectionExpandsRegistry;
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/search/{search}", method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> executeSearchWithFilters(RootResourceInformation rootResourceInformation, DefaultedPageable pageable,
            QSort sort, PersistentEntityResourceAssembler assembler, @PathVariable String search,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "projection", required = false) String projection) {
        Object result = getResult(rootResourceInformation, pageable, sort, filters,  search, projection);
        return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> getCollectionWithFilters(RootResourceInformation rootResourceInformation, DefaultedPageable pageable,
            QSort sort, PersistentEntityResourceAssembler assembler, @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "projection", required = false) String projection) {
        Object result = getResult(rootResourceInformation, pageable, sort, filters,  null, projection);
        return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> getWithFilters(RootResourceInformation rootResourceInformation,
            PersistentEntityResourceAssembler assembler, @PathVariable("id") String id,
            @RequestParam(value = "projection", required = false) String projection) {
        Object result = getSingleResult(rootResourceInformation, id, projection);
        if (result == null)
            throw new ResourceNotFoundException();
        return new ResponseEntity<Object>(assembler.toFullResource(result), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = { "orest", "count" })
    public ResponseEntity<CountResponse> getCountWithFilters(RootResourceInformation rootResourceInformation,
            @RequestParam(value = "filters", required = false) String filters) {
        Object count = getCountResult(rootResourceInformation, filters, null);
        return new ResponseEntity<CountResponse>(new CountResponse(count), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/search/{search}", method = RequestMethod.GET, params = { "orest", "count" })
    public ResponseEntity<CountResponse> executeSearchWithFilters(RootResourceInformation rootResourceInformation, @PathVariable String search,
            @RequestParam(value = "filters", required = false) String filters) {
        Object count = getCountResult(rootResourceInformation, filters, search);
        return new ResponseEntity<CountResponse>(new CountResponse(count), HttpStatus.OK);
    }
    
    private Object getCountResult(RootResourceInformation rootResourceInformation, String filters, String search) {
        ExpressionEntityInformation expEntityInfo = getExpressionEntityInfo(rootResourceInformation.getDomainType());
        RequestBooleanExpressionBuilder requestExpBuilder = new RequestBooleanExpressionBuilder(expEntityInfo, expressionBuilder);
        FilterPart searchMethodPart = filterStringParser.getSearchFilterPart(search, expEntityInfo);
        appendCommons(requestExpBuilder, expEntityInfo, filters, null);
        requestExpBuilder.withSearchMethod(searchMethodPart);

        return expEntityInfo.getPredicateInvoker().invokeCount(requestExpBuilder.getFinalExpression(),
                requestExpBuilder.getPredicateContext());
    }

    private Iterable<Object> getResult(RootResourceInformation rootResourceInformation, DefaultedPageable pageable, QSort sort,
            String filters, String search, String projection) {
        ExpressionEntityInformation expEntityInfo = getExpressionEntityInfo(rootResourceInformation.getDomainType());

        ProjectionInfo projectionInfo = getProjectionInfo(projection, expEntityInfo.getEntityType());

        RequestBooleanExpressionBuilder requestExpBuilder = new RequestBooleanExpressionBuilder(expEntityInfo, expressionBuilder);
        FilterPart searchMethodPart = filterStringParser.getSearchFilterPart(search, expEntityInfo);
        appendCommons(requestExpBuilder, expEntityInfo, filters, projectionInfo);
        requestExpBuilder.withSearchMethod(searchMethodPart);
        Iterable<Object> result;

        boolean addPageable = searchMethodPart != null ? addPageable(expEntityInfo, searchMethodPart.getMethodInfo()) : addPageable(
                expEntityInfo, null);
        result = getResult(expEntityInfo.getPredicateInvoker(), requestExpBuilder, pageable, sort, addPageable);
        return result;
    }

    private ExpressionEntityInformation getExpressionEntityInfo(Class<?> domainType) {
        ExpressionEntityInformation expEntityInfo = entityExpressionMethodsRegistry.getEntityInformation(domainType);
        if (expEntityInfo == null)
            throw new ResourceNotFoundException();
        return expEntityInfo;
    }

    private Object getSingleResult(RootResourceInformation rootResourceInformation, String id,
            String projection) {
        ExpressionEntityInformation expEntityInfo = getExpressionEntityInfo(rootResourceInformation.getDomainType());

        ProjectionInfo projectionInfo = getProjectionInfo(projection, expEntityInfo.getEntityType());

        RequestBooleanExpressionBuilder requestExpBuilder = new RequestBooleanExpressionBuilder(expEntityInfo, expressionBuilder);
        appendCommons(requestExpBuilder, expEntityInfo, null, projectionInfo);
        requestExpBuilder.withId(id);
        return expEntityInfo.getPredicateInvoker().invokeFindOne(requestExpBuilder.getFinalExpression(),
                requestExpBuilder.getPredicateContext());
    }

    private void appendCommons(RequestBooleanExpressionBuilder requestExpBuilder, ExpressionEntityInformation expEntityInfo,
            String filters, ProjectionInfo projectionInfo) {
        FilterPart filtersPartTree = filterStringParser.getFilterPart(filters, expEntityInfo);
        requestExpBuilder.withFilters(filtersPartTree).withStaticFilters();
        if (projectionInfo != null)
            requestExpBuilder.withJoins(projectionInfo.getExpands());
    }

    private Iterable<Object> getResult(QueryDslPredicateInvoker invoker, RequestBooleanExpressionBuilder requestExpBuilder,
            DefaultedPageable pageable, QSort sort, boolean defaultedPageable) {
        Iterable<Object> result;
        if (pageable.getPageable() == null || (pageable.isDefault() && !defaultedPageable))
            result = invoker.invokeFindAll(requestExpBuilder.getFinalExpression(), requestExpBuilder.getPredicateContext(), sort);
        else
            result = invoker.invokeFindAll(requestExpBuilder.getFinalExpression(), requestExpBuilder.getPredicateContext(),
                    pageable.getPageable());
        return result;
    }

    private boolean addPageable(ExpressionEntityInformation expEntityInfo, ExpressionMethodInformation searchMethodInfo) {
        if (!expEntityInfo.isDefaultedPageable())
            return false;
        if (searchMethodInfo != null)
            return searchMethodInfo.isDefaultedPageable();
        return true;
    }

    private ProjectionInfo getProjectionInfo(String projection, Class<?> entityType) {
        ProjectionInfo projectionInfo = projectionExpandsRegistry.get(projection, entityType);
        authorizeProjection(projection, projectionInfo);
        return projectionInfo;
    }

    private void authorizeProjection(String projectionName, ProjectionInfo projectionInfo) {
        if (projectionInfo != null && projectionAuthorizationStrategy != null)
            projectionAuthorizationStrategy.authorize(projectionName, projectionInfo.getProjectionType());
    }
}
