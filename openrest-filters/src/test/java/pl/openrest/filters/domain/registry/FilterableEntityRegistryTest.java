package pl.openrest.filters.domain.registry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;

import pl.openrest.filters.predicate.PredicateRepositoryFactory;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.repository.PredicateContextRepository;

@RunWith(MockitoJUnitRunner.class)
public class FilterableEntityRegistryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Repositories repositories;

    @Mock
    private PredicateRepositoryFactory predicateRepositoryFactory;

    private FilterableEntityRegistry registry;

    @Before
    public void setUp() {
        when(repositories.getRepositoryFor(Object.class)).thenReturn(mock(PredicateContextRepository.class));
        when(predicateRepositoryFactory.create(Mockito.any())).thenReturn(mock(pl.openrest.filters.predicate.PredicateRepository.class));
        when(applicationContext.getBeansWithAnnotation(PredicateRepository.class)).thenReturn(
                Collections.singletonMap("PredicateRepository", (Object) new TestPredicateRepository()));
        registry = new FilterableEntityRegistry(repositories, predicateRepositoryFactory);
        registry.setApplicationContext(applicationContext);
    }

    @Test
    public void shouldCreateRegistryWithPredicateRepositoryAndRepositoryInvoker() throws Exception {
        // given
        // when
        FilterableEntityInformation entityInfo = registry.get(Object.class);
        // then
        Assert.assertNotNull(entityInfo.getPredicateRepository());
        // Assert.assertFalse(entityInfo.getStaticFilters().isEmpty());
        // Assert.assertEquals(PredicateType.SEARCH, entityInfo.getPredicateInformation("userIdEq").getType());
        // Assert.assertEquals("principal.id != null", entityInfo.getStaticFilters().get(0).getCondition());
        // Assert.assertArrayEquals(new String[] { "1", "2" }, entityInfo.getStaticFilters().get(0).getParameters());
        // Assert.assertNotNull(entityInfo.getStaticFilters().get(0).getPredicateInformation());
        // Assert.assertEquals(PredicateType.SEARCH, entityInfo.getPredicateInformation("nameLike").getType());
        // Assert.assertEquals(false, entityInfo.getPredicateInformation("nameLike").isDefaultedPageable());
        // Assert.assertNotNull(entityInfo.getPredicateInformation("nameLike").getMethod());
        //
        // Assert.assertNull(entityInfo.getPredicateInformation("productionYearBetween"));
        // Assert.assertFalse(entityInfo.getPredicateInformation("tagIdEq").getJoins().isEmpty());
        // Assert.assertFalse(entityInfo.getPredicateInformation("tagIdEq").getJoins().get(0).isFetch());
    }

    @PredicateRepository(value = Object.class)
    public class TestPredicateRepository {

        // @StaticFilter(offOnCondition = "principal.id != null", parameters = { "1", "2" })
        // public BooleanExpression productionYearBetween(Integer from, Integer to) {
        // return null;
        // }
        //
        // @Predicate(type = PredicateType.SEARCH)
        // public BooleanExpression userIdEq(Long userId) {
        // return null;
        // }
        //
        // @Predicate(type = PredicateType.SEARCH, defaultedPageable = false)
        // public BooleanExpression nameLike(String name) {
        // return null;
        // }
        //
        // @Predicate(joins = @Join(value = "tags"))
        // public BooleanExpression tagIdEq(Long tagId) {
        // return null;
        // }
        //
        // @Predicate(type = PredicateType.SORT)
        // public NumberExpression<Integer> tagsSize() {
        // return null;
        // }

    }

    //

}
