package pl.openrest.filters.predicate.parser;

import org.junit.Assert;
import org.junit.Test;

import pl.openrest.predicate.parser.DefaultPredicatePartsExtractor;
import pl.openrest.predicate.parser.PredicateParts;

public class DefaultPredicatePartsExtractorTest {

    private DefaultPredicatePartsExtractor extractor = new DefaultPredicatePartsExtractor();

    @Test
    public void shouldExtractPredicateNameAndParameters() throws Exception {
        // given
        String predicate = "yearBetween(1920;1930)";
        // when
        PredicateParts parts = extractor.extractParts(predicate);
        // then
        Assert.assertEquals("yearBetween", parts.getPredicateName());
        Assert.assertArrayEquals(new String[] { "1920", "1930" }, parts.getParameters());
    }

    @Test
    public void shouldReturnEmptyParameters() throws Exception {
        // given
        String predicate = "isActive";
        // when
        PredicateParts parts = extractor.extractParts(predicate);
        // then
        Assert.assertEquals(0, parts.getParameters().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionOnPredicateWrongFormat() throws Exception {
        // given
        String predicate = "()";
        // when
        PredicateParts parts = extractor.extractParts(predicate);
        // then
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionOnNotClosedBrackets() throws Exception {
        // given
        String predicate = "between(1;2";
        // when
        PredicateParts parts = extractor.extractParts(predicate);
        // then
    }
}
