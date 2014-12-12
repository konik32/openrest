package openrest.config.httpquery.parser;

import java.util.List;

import openrest.httpquery.parser.Parsers;
import openrest.httpquery.parser.RequestParsingException;
import openrest.httpquery.parser.TempPart;
import openrest.httpquery.parser.Parsers.PathWrapper;
import openrest.httpquery.parser.Parsers.SubjectWrapper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mapping.PropertyPath;

import static org.junit.Assert.*;

public class ParsersUnitTest {

	private PathWrapper pathWrapper;
	private SubjectWrapper subjectWrapper;
	private List<PropertyPath> expand;

	@Before
	public void setUp() {

		String path = "/user/1/address";
		String subject = "distinct, count";
		String views = "address, address.city";

		pathWrapper = Parsers.parsePath(path);
		subjectWrapper = Parsers.parseSubject(subject);
		expand = Parsers.parseExpand(views, User.class,null);
	}

//	@Test
//	public void testFilterStrParsing() {
//		String filter = "eq(id,1) ;and; between(depth,1,2) ;or; eq(id,1) ;and; between(depth,1,2)";
//		TempPart tempPart = Parsers.parseFilter(filter);
//		assertEquals(1, tempPart.getParts().size());
//		assertEquals(2, tempPart.getParts().get(0).getParts().size());
//		assertEquals(2, tempPart.getParts().get(0).getParts().get(0).getParts().size());
//	}

	@Test(expected = RequestParsingException.class)
	public void doesThrowParsingExceptionOnFunctionWithoutBrackets() {
		String filter = "eq";
		Parsers.parseFilter(filter);
	}

	@Test(expected = RequestParsingException.class)
	public void doesThrowParsingExceptionOnFunctionWithoutParameters() {
		String filter = "eq()";
		Parsers.parseFilter(filter);
	}

	@Test(expected = RequestParsingException.class)
	public void doesThrowParsingExceptionOnSingleOrCondition() {
		String filter = "eq(id,1);or;;or;";
		Parsers.parseFilter(filter);
	}

	@Test(expected = RequestParsingException.class)
	public void doesThrowParsingExceptionOnSingleAndCondition() {
		String filter = "eq(id;1) ;and;";
		Parsers.parseFilter(filter);
	}

	@Test
	public void testPathParsing() {
		assertEquals("1", pathWrapper.getId());
		assertEquals("address", pathWrapper.getProperty());
	}

	@Test
	public void testSubjectParsing() {
		assertTrue(subjectWrapper.getCountProjection());
		assertTrue(subjectWrapper.getDistinct());
	}

	@Test
	public void testViewsParsing() {
		assertEquals(Address.class, expand.get(0).getType());
		assertEquals(String.class, expand.get(1).getLeafProperty().getType());
	}

	public class User {
		private Address address;

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}
	}

	public class Address {
		private String city;

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}
	}

}
