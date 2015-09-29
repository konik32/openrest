package pl.openrest.filters.predicate.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pl.openrest.predicate.parser.DefaultFilterTreeBuilder;
import pl.openrest.predicate.parser.FilterPart;
import pl.openrest.predicate.parser.FilterPart.FilterPartType;
import pl.openrest.predicate.parser.PredicateParts;
import pl.openrest.predicate.parser.PredicatePartsExtractor;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFilterTreeBuilderTest {

    @Mock
    private PredicatePartsExtractor predicatePartsExtractor;

    private DefaultFilterTreeBuilder builder;

    @Before
    public void setUp() {
        builder = new DefaultFilterTreeBuilder(predicatePartsExtractor);
        Mockito.when(predicatePartsExtractor.extractParts(Mockito.anyString())).thenReturn(Mockito.mock(PredicateParts.class));
    }

    @Test
    public void shouldBuildTree() throws Exception {
        // given
        String filters = "yearBetween(2;3);and;rankFrom(2;6);or;voteFrom(3;4)";
        // when
        FilterPart tree = builder.from(filters);
        // then
        assertEquals(tree.getType(), FilterPart.FilterPartType.OR);
        assertEquals(tree.getParts().size(), 2);
        for (FilterPart part : tree.getParts()) {
            assertEquals(part.getType(), FilterPartType.AND);
            for (FilterPart p : part.getParts()) {
                assertEquals(p.getType(), FilterPartType.LEAF);
                assertNotNull(p.getPredicateParts());
            }
        }
    }

    @Test
    public void shouldReturnNullOnEmptyString() throws Exception {
        // given
        String filters = "";
        // when
        // then
        assertNull(builder.from(filters));
    }
}
