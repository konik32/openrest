package orest.dto.validation;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import lombok.Data;
import orest.dto.validation.annotation.ValidateExpression;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.validation.Errors;

@RunWith(MockitoJUnitRunner.class)
public class DtoFieldExpressionValidatorTest {

	@Mock
	private UpdateValidationContext updateValidationContext;

	@Mock
	private BeanFactory beanFactory;
	@InjectMocks
	private DtoFieldExpressionValidator validator;

	private ADto dto = new ADto();
	private AEntity entity = new AEntity();

	private Errors errors;

	@Before
	public void setUp() {
		when(updateValidationContext.getEntity()).thenReturn(entity);
		when(updateValidationContext.getDto()).thenReturn(dto);
		errors = new DtoValidationErrors(dto.getClass().getSimpleName(), dto);
	}

	@Test
	public void shouldNotCreateAnyErrors() throws Exception {
		// given
		// when
		validator.validate(dto, errors);
		// then
		assertFalse(errors.getErrorCount() > 0);
	}

	@Data
	public static class ADto {
		@ValidateExpression("#{dto.name.equals('aDto') && entity.name.equals('aEntity')}")
		private String name = "aDto";
		@Valid
		private BDto b;
	}

	@Data
	public static class BDto {
		@ValidateExpression("#{dto.name.equals('bDto')&& entity.name.equals('bEntity')}")
		private String name = "bDto";
		@ValidateExpression("#{dto.id.equals(1l)}")
		private Long id = 1l;
		@Valid
		private CDto c = new CDto();

		public BDto(String name) {
			this.name = name;
		}
	}

	@Data
	public static class CDto {
		@ValidateExpression("#{dto.name.equals('cDto') && entity.name.equals('cEntity')}")
		private String name = "cDto";
	}

	@Data
	public static class AEntity {
		private String name = "aEntity";
		private BEntity b = new BEntity();
	}

	@Data
	public static class BEntity {
		private String name = "bEntity";
		private CEntity c = new CEntity();
	}

	@Data
	public static class CEntity {
		private String name = "cEntity";
	}
}
