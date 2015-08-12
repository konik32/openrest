package orest.util.traverser;

import static org.junit.Assert.*;
import orest.util.traverser.PathBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PathBuilderTest {

	@Test
	public void shouldAppendCollection() throws Exception {
		// given
		String currentPath = "addresses";
		// when
		String path = PathBuilder.appendTo(currentPath, 1);
		// then
		assertEquals("addresses[1]", path);
	}

	@Test
	public void shouldAppendMap() throws Exception {
		// given
		String currentPath = "addresses";
		// when
		String path = PathBuilder.appendToMap(currentPath, "home");
		// then
		assertEquals("addresses(home)", path);
	}

	@Test
	public void shouldAppendFieldNameWithoutDotOnEmptyOrNullCurrentPath() throws Exception {
		// given
		String currentPath = "";
		String currentPath2 = null;
		// when
		String path = PathBuilder.appendTo(currentPath, "address");
		String path2 = PathBuilder.appendTo(currentPath2, "product");
		// then
		assertEquals("address", path);
		assertEquals("product", path2);
	}
	
	@Test
	public void shouldAppendFieldNameWithDotOnNonEmptyCurrentPath() throws Exception {
		// given
		String currentPath = "address";
		// when
		String path = PathBuilder.appendTo(currentPath, "city");
		// then
		assertEquals("address.city", path);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionOnEmptyCurrentPath() throws Exception {
		// given
		String currentPath = "";
		// when
		PathBuilder.appendToMap(currentPath, "home");
		// then
	}

}
