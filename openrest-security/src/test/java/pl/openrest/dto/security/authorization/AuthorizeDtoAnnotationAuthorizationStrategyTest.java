package pl.openrest.dto.security.authorization;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;

import pl.openrest.dto.security.authorization.annotation.AuthorizeDto;
import pl.openrest.exception.OrestException;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizeDtoAnnotationAuthorizationStrategyTest {

    private AuthorizeDtoAnnotationAuthorizationStrategy authorizationStrategy;

    @Mock
    private DtoAuthorizationStrategyFactory strategyFactory;

    @Mock
    private AccessDecisionManager accessDecisionManager;

    private UserDetails principal;
    private Object dto;
    private Object entity;

    @Before
    public void setUp() {
        principal = mock(UserDetails.class);
        dto = mock(TestDto.class);
        entity = mock(Object.class);
        authorizationStrategy = new AuthorizeDtoAnnotationAuthorizationStrategy(strategyFactory, accessDecisionManager);
    }

    @Test
    public void shouldAbstainOnMissingAuthStrategiesAnn() throws Exception {
        // given
        // when
        assertEquals(0, authorizationStrategy.isAuthorized(principal, mock(Object.class), entity));
        // then
    }

    @Test(expected = OrestException.class)
    public void shouldThrowOrestExceptionOnNoStrategyInStrategyFactory() throws Exception {
        // given
        // when
        authorizationStrategy.isAuthorized(principal, dto, entity);
        // then
    }

    @Test
    public void shouldPassParentAuthorizationStrategyToAccessDecitionManager() throws Exception {
        // given
        dto = mock(ChildTestDto.class);
        when(strategyFactory.getAuthorizationStrategy(TestStrategy.class)).thenReturn(new TestStrategy());
        ParentTestStrategy parentStrategy = new ParentTestStrategy();
        when(strategyFactory.getAuthorizationStrategy(ParentTestStrategy.class)).thenReturn(parentStrategy);
        // when
        authorizationStrategy.isAuthorized(principal, dto, entity);
        // then
        ArgumentCaptor<List> strategiesCaptor = ArgumentCaptor.forClass(List.class);
        verify(accessDecisionManager, times(1)).isAuthorized(Matchers.eq(principal), Matchers.eq(dto), Matchers.eq(entity),
                strategiesCaptor.capture());
        Assert.assertTrue(strategiesCaptor.getValue().contains(parentStrategy));
    }

    class TestStrategy implements DtoAuthorizationStrategy<UserDetails, Object, Object> {

        @Override
        public int isAuthorized(UserDetails principal, Object dto, Object entity) {
            return 1;
        }

    }

    class ParentTestStrategy implements DtoAuthorizationStrategy<UserDetails, Object, Object> {

        @Override
        public int isAuthorized(UserDetails principal, Object dto, Object entity) {
            return 1;
        }

    }

    @AuthorizeDto({ TestStrategy.class, DtoAuthorizationStrategy.class })
    class TestDto {

    }

    @AuthorizeDto({ TestStrategy.class })
    class ChildTestDto extends ParentTestDto {

    }

    @AuthorizeDto({ ParentTestStrategy.class })
    class ParentTestDto {

    }
}
