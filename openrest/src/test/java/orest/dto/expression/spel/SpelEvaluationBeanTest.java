package orest.dto.expression.spel;

import lombok.Data;
import orest.dto.expression.spel.DtoEvaluationWrapper;
import orest.dto.expression.spel.SpelEvaluatorBean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;

@RunWith(MockitoJUnitRunner.class)
public class SpelEvaluationBeanTest {

	private SpelEvaluatorBean spelEvaluatorBean;

	@Mock
	private BeanFactory beanFactory;

	@Before
	public void setUp() {
		TestBean bean = new TestBean();
		when(beanFactory.getBean("testBean")).thenReturn(bean);
		spelEvaluatorBean = new SpelEvaluatorBean(beanFactory);
	}

	@Test
	public void shouldSetValues() {
		UserDto userDto = new UserDto();
		spelEvaluatorBean.evaluate(new DtoEvaluationWrapper(userDto, null));
		assertEquals("staszek", userDto.getName());
		assertEquals(true, userDto.getAuthenticated());
		assertSame(100L, userDto.getId());
	}
	
	
	
	public static class TestBean {

		public Boolean isAuthenticated() {
			return true;
		}
	}

	@Data
	public class UserDto {

		@Value("#{'staszek'}")
		private String name;
		
		@Value("#{@testBean.isAuthenticated()}")
		private Boolean authenticated;
		
		@Value("#{dto.number * 10}")
		private Long id;
		
		private final Long number = 10L;
		
		
		
	}
}
