package pl.openrest.dto.security.authorization;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("rawtypes")
public class AffirmativeAccessDecitionManagerTest {

    private AffirmativeAccessDecisionManager manager;

    @Mock
    private DtoAuthorizationStrategy grantingAccessStrategy;
    @Mock
    private DtoAuthorizationStrategy abstainingAccessStrategy;
    @Mock
    private DtoAuthorizationStrategy denyingAccessStrategy;

    @Mock
    private Object strategyParam;

    @Before
    public void setUp() {
        manager = new AffirmativeAccessDecisionManager();
        Mockito.when(grantingAccessStrategy.isAuthorized(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(abstainingAccessStrategy.isAuthorized(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0);
        Mockito.when(denyingAccessStrategy.isAuthorized(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(-1);
    }

    @Test
    public void shouldGrantAccessOnyAnyGrantingStrategy() throws Exception {
        // given
        List<DtoAuthorizationStrategy> strategies = Arrays.asList(abstainingAccessStrategy, denyingAccessStrategy, grantingAccessStrategy);
        // when
        int result = manager.isAuthorized(strategyParam, strategyParam, strategyParam, strategies);
        // then
        Assert.assertEquals(1, result);
    }

    @Test
    public void shouldAbstainOnAllAbstainingStrategies() throws Exception {
        // given
        List<DtoAuthorizationStrategy> strategies = Arrays.asList(abstainingAccessStrategy, abstainingAccessStrategy,
                abstainingAccessStrategy);
        // when
        int result = manager.isAuthorized(strategyParam, strategyParam, strategyParam, strategies);
        // then
        Assert.assertEquals(0, result);
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldDenyAccessOnAllAbstainingStrategiesAndFlagIsSetToFalse() throws Exception {
        // given
        manager.setAllowIfAllAbstainDecisions(false);
        List<DtoAuthorizationStrategy> strategies = Arrays.asList(abstainingAccessStrategy, abstainingAccessStrategy,
                abstainingAccessStrategy);
        // when
        int result = manager.isAuthorized(strategyParam, strategyParam, strategyParam, strategies);
        // then
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldDenyAccessOnNoGrantingStrategyAndMoreThanOneDenyingStrategy() throws Exception {
        // given
        manager.setAllowIfAllAbstainDecisions(false);
        List<DtoAuthorizationStrategy> strategies = Arrays
                .asList(abstainingAccessStrategy, abstainingAccessStrategy, denyingAccessStrategy);
        // when
        int result = manager.isAuthorized(strategyParam, strategyParam, strategyParam, strategies);
    }
}
