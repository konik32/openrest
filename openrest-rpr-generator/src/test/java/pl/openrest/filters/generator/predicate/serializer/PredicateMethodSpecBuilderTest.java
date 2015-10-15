package pl.openrest.filters.generator.predicate.serializer;

import java.time.LocalDateTime;

import javax.lang.model.element.Modifier;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pl.openrest.filters.generator.predicate.context.PredicateInformationFactory.ParameterInformation;
import pl.openrest.filters.generator.predicate.serializer.JavaRemotePredicateRepositorySerializer.PredicateMethodSpecBuilder;
import pl.openrest.filters.remote.predicate.FilterPredicate;
import pl.openrest.filters.remote.predicate.SearchPredicate;

import com.squareup.javapoet.MethodSpec;

@RunWith(MockitoJUnitRunner.class)
public class PredicateMethodSpecBuilderTest {

    @Test
    public void shouldCreateMethodSpec() throws Exception {
        // given
        String methodNameFieldName = "USER_ID_EQ";
        String methodName = "userIdEq";
        PredicateMethodSpecBuilder builder = new PredicateMethodSpecBuilder(new FilterPredicate(methodName, new ParameterInformation("id",
                Long.class), new ParameterInformation("date", LocalDateTime.class)), methodNameFieldName);

        // when
        MethodSpec spec = builder.build();
        // then
        Assert.assertEquals(methodName, spec.name);
        Assert.assertThat(spec.modifiers, Matchers.contains(Modifier.PUBLIC, Modifier.STATIC));
        Assert.assertThat(spec.parameters, Matchers.hasSize(2));
        Assert.assertEquals(FilterPredicate.class.getName(), spec.returnType.toString());
        Assert.assertEquals(String.format("return new pl.openrest.filters.remote.predicate.FilterPredicate(%s,%s,%s);\n",
                methodNameFieldName, "id", "date"), spec.code.toString());
    }

    @Test
    public void shouldCreateMethodSpecWithSearchPredicateReturnStatement() throws Exception {
        // given
        String methodNameFieldName = "USER_ID_EQ";
        String methodName = "userIdEq";
        PredicateMethodSpecBuilder builder = new PredicateMethodSpecBuilder(new SearchPredicate(methodName, false,
                new ParameterInformation("id", Long.class)), methodNameFieldName);

        // when
        MethodSpec spec = builder.build();
        // then
        Assert.assertEquals(SearchPredicate.class.getName(), spec.returnType.toString());
        Assert.assertEquals(String.format("return new pl.openrest.filters.remote.predicate.SearchPredicate(%s,%s,%s);\n",
                methodNameFieldName, "false", "id"), spec.code.toString());
    }

    @Test
    public void shouldCreateMethodSpecWithSearchPredicateWithoutParameters() throws Exception {
        // given
        String methodNameFieldName = "USER_ID_EQ";
        String methodName = "userIdEq";
        PredicateMethodSpecBuilder builder = new PredicateMethodSpecBuilder(new SearchPredicate(methodName, false), methodNameFieldName);

        // when
        MethodSpec spec = builder.build();
        // then
        Assert.assertEquals(SearchPredicate.class.getName(), spec.returnType.toString());
        Assert.assertEquals(
                String.format("return new pl.openrest.filters.remote.predicate.SearchPredicate(%s,%s);\n", methodNameFieldName, "false"),
                spec.code.toString());
    }

    @Test
    public void shouldCreateMethodSpecWithFilterPredicateWithoutParameters() throws Exception {
        // given
        String methodNameFieldName = "USER_ID_EQ";
        String methodName = "userIdEq";
        PredicateMethodSpecBuilder builder = new PredicateMethodSpecBuilder(new FilterPredicate(methodName), methodNameFieldName);

        // when
        MethodSpec spec = builder.build();
        // then
        Assert.assertEquals(FilterPredicate.class.getName(), spec.returnType.toString());
        Assert.assertEquals(String.format("return new pl.openrest.filters.remote.predicate.FilterPredicate(%s);\n", methodNameFieldName),
                spec.code.toString());
    }
}
