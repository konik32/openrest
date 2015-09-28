package pl.openrest.dto.security.authorization;

import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("rawtypes")
public class DtoAuthorizationStrategyMappingHandlerTest {

    private DtoAuthorizationStrategyMappingHandler handler;

    @Mock
    private AccessDecisionManager accessDecisionManager;

    @Before
    public void setUp() {
        handler = new DtoAuthorizationStrategyMappingHandler(accessDecisionManager);
    }

    @Test
    public void shouldCallAccessDecisionMangerWithDtoEntityAndStrategiesParameters() throws Exception {
        // given
  
        DtoAuthorizationStrategy strategy1 = mock(DtoAuthorizationStrategy.class);
        DtoAuthorizationStrategy strategy2 = mock(DtoAuthorizationStrategy.class);
        handler.addStrategy(strategy1);
        handler.addStrategy(strategy2);
        Object dto = Mockito.mock(Object.class);
        Object entity = Mockito.mock(Object.class);
        // when
        handler.handle(dto, entity);
        // then
        ArgumentCaptor<List> strategiesCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(accessDecisionManager, Mockito.times(1)).isAuthorized(Mockito.eq(null), Mockito.eq(dto), Mockito.eq(entity),
                strategiesCaptor.capture());
        Assert.assertEquals(2, strategiesCaptor.getValue().size());
    }
}
