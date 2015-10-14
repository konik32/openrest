package pl.openrest.filters.generator.predicate.serializer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.openrest.filters.client.predicate.AbstractPredicate;
import pl.openrest.filters.client.predicate.SearchPredicate;
import pl.openrest.filters.generator.predicate.context.PredicateInformationFactory.ParameterInformation;
import pl.openrest.filters.generator.predicate.context.PredicateRepositoryInformation;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

@RequiredArgsConstructor
public class JavaRemotePredicateRepositorySerializer implements RemotePredicateRepositorySerializer {

    private final File outputDirectory;
    private final RemotePredicateRepositoryNamingStrategy namingStrategy;

    private static String SEARCH_PREDICATE_WITH_PARAMS_FORMAT = "return new $T($L,$L,$L)";
    private static String SEARCH_PREDICATE_WITHOUT_PARAMS_FORMAT = "return new $T($L,$L)";
    private static String FILTER_PREDICATE_WITH_PARAMS_FORMAT = "return new $T($L,$L)";
    private static String FILTER_PREDICATE_WITHOUT_PARAMS_FORMAT = "return new $T($L)";

    @Override
    public void serialize(List<PredicateRepositoryInformation> repositories) throws IOException {
        for (PredicateRepositoryInformation repoInfo : repositories) {
            String className = namingStrategy.getClassName(repoInfo.getRepositoryType().getSimpleName());
            MethodSpec constuctor = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();

            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(className);
            addPredicateMethodSpecs(repoInfo.getPredicates(), typeBuilder);
            typeBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL).addMethod(constuctor)
                    .addAnnotation(JavaSerializerUtils.createGeneratedAnnotationSpec())
                    .addField(JavaSerializerUtils.createDefaultedPageableField(repoInfo.isDefaultedPageable()));

            JavaFile file = JavaFile.builder(namingStrategy.getPackageName(repoInfo.getRepositoryType().getPackage().getName()),
                    typeBuilder.build()).build();

            file.writeTo(outputDirectory);
        }
    }

    private void addPredicateMethodSpecs(List<AbstractPredicate> predicates, TypeSpec.Builder typeBuilder) {
        for (AbstractPredicate predicate : predicates) {
            FieldSpec methodNameStaticField = JavaSerializerUtils.createMethodNameStaticField(predicate.getName());
            typeBuilder.addField(methodNameStaticField);
            PredicateMethodSpecBuilder methodBuilder = new PredicateMethodSpecBuilder(predicate, methodNameStaticField.name);
            typeBuilder.addMethod(methodBuilder.build());
        }
    }

    public static class PredicateMethodSpecBuilder {

        private final AbstractPredicate predicate;
        private final String methodNameFieldName;

        public PredicateMethodSpecBuilder(@NonNull AbstractPredicate predicate, @NonNull String methodNameFieldName) {
            this.predicate = predicate;
            this.methodNameFieldName = methodNameFieldName;
        }

        public MethodSpec build() {
            List<ParameterSpec> parameterSpecs = createParameterSpecs();
            CodeBlock codeBlock = createMethodCodeBlock(parameterSpecs);
            return MethodSpec.methodBuilder(predicate.getName()).addParameters(parameterSpecs).returns(predicate.getClass())
                    .addCode(codeBlock).addModifiers(Modifier.PUBLIC, Modifier.STATIC).build();
        }

        private CodeBlock createMethodCodeBlock(List<ParameterSpec> parameterSpecs) {
            String parameterNames[] = JavaSerializerUtils.getParameterNames(parameterSpecs);

            Builder builder = CodeBlock.builder();
            if (predicate instanceof SearchPredicate) {
                if (parameterNames.length > 0)
                    builder.addStatement(SEARCH_PREDICATE_WITH_PARAMS_FORMAT, predicate.getClass(), methodNameFieldName,
                            ((SearchPredicate) predicate).isDefaultedPageable(), String.join(",", parameterNames));
                else
                    builder.addStatement(SEARCH_PREDICATE_WITHOUT_PARAMS_FORMAT, predicate.getClass(), methodNameFieldName,
                            ((SearchPredicate) predicate).isDefaultedPageable());
            } else {
                if (parameterNames.length > 0)
                    builder.addStatement(FILTER_PREDICATE_WITH_PARAMS_FORMAT, predicate.getClass(), methodNameFieldName,
                            String.join(",", parameterNames));
                else
                    builder.addStatement(FILTER_PREDICATE_WITHOUT_PARAMS_FORMAT, predicate.getClass(), methodNameFieldName);
            }
            return builder.build();
        }

        private List<ParameterSpec> createParameterSpecs() {
            ParameterInformation parameters[] = Arrays.copyOf(predicate.getParameters(), predicate.getParameters().length,
                    ParameterInformation[].class);
            return Arrays.asList(JavaSerializerUtils.createParameters(parameters));
        }

    }
}
