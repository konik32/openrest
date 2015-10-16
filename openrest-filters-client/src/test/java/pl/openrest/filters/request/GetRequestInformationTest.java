package pl.openrest.filters.request;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pl.openrest.filters.remote.predicate.FilterPredicate;
import pl.openrest.filters.remote.predicate.ParameterSerializer;
import pl.openrest.filters.remote.predicate.SearchPredicate;
import pl.openrest.filters.request.GetRequestInformation.GetRequestInformationBuilder;

@RunWith(MockitoJUnitRunner.class)
public class GetRequestInformationTest {

    private GetRequestInformationBuilder builder;

    @Mock
    private ParameterSerializer defaultSerializer;

    @Before
    public void setUp() {
        GetRequestInformationBuilder.setDefualtSerializer(defaultSerializer);

        builder = new GetRequestInformationBuilder("path");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldBuilderThrowExceptionWhenBothIdAndSearchAreUsed() throws Exception {
        // given
        Mockito.when(defaultSerializer.serialize(Mockito.any())).thenReturn("1");
        // when
        builder.id(1l).search("findById").build();
        // then
    }

    @Test
    public void shouldBuilderAppendAllParameters() throws Exception {
        // given
        FilterPredicate filterPredicate = Mockito.mock(FilterPredicate.class);
        Mockito.when(filterPredicate.toString(defaultSerializer)).thenReturn("active");
        Mockito.when(defaultSerializer.serialize("John")).thenReturn("John");
        // when
        GetRequestInformation requestInfo = builder.filter(filterPredicate).page(1).size(2).projection("default").parameter("name", "John")
                .build();
        // then
        Assert.assertThat(
                requestInfo.getParametersString(),
                Matchers.allOf(Matchers.containsString("filter=active"), Matchers.containsString("page=1"),
                        Matchers.containsString("size=2"), Matchers.containsString("projection=default"),
                        Matchers.containsString("name=John")));
    }

    @Test
    public void shouldBuilderAppendSearchPredicate() throws Exception {
        // given
        SearchPredicate predicate = Mockito.mock(SearchPredicate.class);
        Mockito.when(predicate.toString(defaultSerializer)).thenReturn("yearBetween(1;2)");
        // when
        GetRequestInformation requestInfo = builder.search(predicate).build();
        // then
        Assert.assertEquals("path/search/yearBetween(1;2)", requestInfo.getPath());
    }

    @Test
    public void shouldBuilderAppendSearchString() throws Exception {
        // given
        // when
        GetRequestInformation requestInfo = builder.search("findByUserId").build();
        // then
        Assert.assertEquals("path/search/findByUserId", requestInfo.getPath());
    }

    @Test
    public void shouldBuilderAppendMultipleFilters() throws Exception {
        // given
        FilterPredicate activePredicate = Mockito.mock(FilterPredicate.class);
        FilterPredicate validPredicate = Mockito.mock(FilterPredicate.class);
        Mockito.when(activePredicate.toString(defaultSerializer)).thenReturn("active");
        Mockito.when(validPredicate.toString(defaultSerializer)).thenReturn("valid");
        // when
        GetRequestInformation requestInfo = builder.filter(activePredicate,validPredicate).build();
        // then
        Assert.assertThat(requestInfo.getParametersString(),
                Matchers.allOf(Matchers.containsString("filter=active"), Matchers.containsString("filter=valid")));
    }

    @Test
    public void shouldBuilderAppendMultipleParametersWithSameName() throws Exception {
        // given
        Mockito.when(defaultSerializer.serialize("John")).thenReturn("John");
        Mockito.when(defaultSerializer.serialize("Doe")).thenReturn("Doe");
        // when
        GetRequestInformation requestInfo = builder.parameter("name", "John", "Doe").build();
        // then
        Assert.assertThat(requestInfo.getParametersString(),
                Matchers.allOf(Matchers.containsString("name=John"), Matchers.containsString("name=Doe")));
    }
}
