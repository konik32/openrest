package pl.openrest.filters.domain.registry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.query.annotation.Join;
import pl.openrest.filters.query.annotation.StaticFilter;
import pl.openrest.filters.repository.PredicateContextRepository;

import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.NumberExpression;

@RunWith(MockitoJUnitRunner.class)
public class FilterableEntityRegistryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Repositories repositories;

    private FilterableEntityRegistry registry;

    @Before
    public void setUp() {
        when(repositories.getRepositoryFor(Product.class)).thenReturn(mock(PredicateContextRepository.class));
        when(applicationContext.getBeansWithAnnotation(PredicateRepository.class)).thenReturn(
                Collections.singletonMap("ProductExpressions", (Object) new ProductPredicates()));
        registry = new FilterableEntityRegistry(repositories);
        registry.setApplicationContext(applicationContext);
    }

    @Test
    public void shouldCreateRegistryWithPredicatesInformationAndStaticFiltersInformation() throws Exception {
        // given
        // when
        FilterableEntityInformation entityInfo = registry.get(Product.class);
        // then
        Assert.assertNotNull(entityInfo.getPredicateRepository());
        Assert.assertNotNull(entityInfo.getRepositoryInvoker());
        Assert.assertFalse(entityInfo.getStaticFilters().isEmpty());
        Assert.assertEquals(PredicateType.SEARCH, entityInfo.getPredicateInformation("userIdEq").getType());
        Assert.assertEquals("principal.id != null", entityInfo.getStaticFilters().get(0).getCondition());
        Assert.assertArrayEquals(new String[] { "1", "2" }, entityInfo.getStaticFilters().get(0).getParameters());
        Assert.assertNotNull(entityInfo.getStaticFilters().get(0).getPredicateInformation());
        Assert.assertEquals(PredicateType.SEARCH, entityInfo.getPredicateInformation("nameLike").getType());
        Assert.assertEquals(false, entityInfo.getPredicateInformation("nameLike").isDefaultedPageable());
        Assert.assertNotNull(entityInfo.getPredicateInformation("nameLike").getMethod());

        Assert.assertNull(entityInfo.getPredicateInformation("productionYearBetween"));
        Assert.assertFalse(entityInfo.getPredicateInformation("tagIdEq").getJoins().isEmpty());
        Assert.assertFalse(entityInfo.getPredicateInformation("tagIdEq").getJoins().get(0).isFetch());
    }

    @PredicateRepository(value = Product.class)
    public class ProductPredicates {

        @StaticFilter(offOnCondition = "principal.id != null", parameters = { "1", "2" })
        public BooleanExpression productionYearBetween(Integer from, Integer to) {
            return null;
        }

        @Predicate(type = PredicateType.SEARCH)
        public BooleanExpression userIdEq(Long userId) {
            return null;
        }

        @Predicate(type = PredicateType.SEARCH, defaultedPageable = false)
        public BooleanExpression nameLike(String name) {
            return null;
        }

        @Predicate(joins = @Join(value = "tags"))
        public BooleanExpression tagIdEq(Long tagId) {
            return null;
        }

        @Predicate(type = PredicateType.SORT)
        public NumberExpression<Integer> tagsSize() {
            return null;
        }

    }

    @Getter
    @Setter
    public class Product {

        private String name;

        private String description;

        private Integer productionYear;

        private List<String> tags;
    }

}
