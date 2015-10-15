package pl.openrest.filters.generator.predicate.context;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.remote.predicate.AbstractPredicate;

@RunWith(MockitoJUnitRunner.class)
public class PredicateRepositoryInformationFactoryTest {

    @Mock
    private PredicateInformationFactory predicateInfoFactory;

    private PredicateRepositoryInformationFactory factory;

    @Before
    public void setUp() {
        factory = new PredicateRepositoryInformationFactory(predicateInfoFactory);
    }

    @Test
    public void shouldCreatePredicateRepositoryInformationFromPredicateRepositoryClass() throws Exception {
        // given
        AbstractPredicate predicate = Mockito.mock(AbstractPredicate.class);
        Mockito.when(predicateInfoFactory.from(Mockito.any(Method.class))).thenReturn(predicate);
        // when
        PredicateRepositoryInformation repoInfo = factory.from(TestPredicateRepository.class);
        // then
        Assert.assertEquals(false, repoInfo.isDefaultedPageable());
        Assert.assertEquals(TestPredicateRepository.class, repoInfo.getRepositoryType());
        Assert.assertEquals(Object.class, repoInfo.getEntityType());
        Assert.assertArrayEquals(new AbstractPredicate[] { predicate }, repoInfo.getPredicates().toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNoPredicateRepositoryAnnotation() throws Exception {
        // given
        // when
        factory.from(Object.class);
        // then
    }

    @PredicateRepository(defaultedPageable = false, value = Object.class)
    public static class TestPredicateRepository {

        @Predicate
        public void testMethod() {

        }

        public void testMethod(Long id) {

        }

    }
}
