package orest.dto.expression.spel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import orest.dto.Dto;
import orest.dto.Dto.DtoType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
		userDto.setAddresses(Arrays.asList(new Address()));
		spelEvaluatorBean.handle(userDto, null);
		assertEquals("staszek", userDto.getName());
		assertEquals(true, userDto.getAuthenticated());
		assertSame(100L, userDto.getId());
		assertEquals("warszawa", userDto.getAddresses().get(0).getCity());
		assertEquals("krakowska", userDto.getAddresses().get(0).getStreet());
		assertEquals("warszawa krakowska", userDto.getAddresses().get(0).getWholeAddress());
	}

	@Test
	public void shouldNotEvaluatedNonEvaluate() throws Exception {
		// given
		UserDto userDto = new UserDto();
		userDto.setPersonalData(new PersonalData());
		// when
		spelEvaluatorBean.handle(userDto, null);
		// then
		assertNull(userDto.getPersonalData().getSurname());
	}

	public static class TestBean {

		public Boolean isAuthenticated() {
			return true;
		}
	}

	@Data
	@Dto(entityType = Object.class, type = DtoType.CREATE)
	public class UserDto {

		@Value("#{'staszek'}")
		private String name;

		@Value("#{@testBean.isAuthenticated()}")
		private Boolean authenticated;

		@Value("#{dto.number * 10}")
		private Long id;

		private final Long number = 10L;

		@Evaluate
		private List<Address> addresses;

		private PersonalData personalData;
		

	}

	@Data
	@Dto(entityType = Object.class, type = DtoType.CREATE)
	public class Address {

		@Value("#{'warszawa'}")
		private String city;

		@Value("#{'krakowska'}")
		private String street;
		
		@Value("#{dto.city + ' ' + dto.street}")
		private String wholeAddress;

	}

	@Data
	public class PersonalData {

		@Value("#{'Kowalski'}")
		private String surname;

	}

}
