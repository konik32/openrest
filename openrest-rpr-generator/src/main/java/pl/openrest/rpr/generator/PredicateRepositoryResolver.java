package pl.openrest.rpr.generator;

import java.lang.reflect.Method;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import lombok.RequiredArgsConstructor;

import org.reflections.Reflections;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.ConfigurationAware;
import pl.openrest.generator.commons.RemoteClassNamingStrategy;
import pl.openrest.generator.commons.type.TypeFileWriter;
import pl.openrest.generator.commons.type.TypeResolver;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

@RequiredArgsConstructor
public class PredicateRepositoryResolver implements TypeResolver, ConfigurationAware {

    private static final String GENERATOR_NAME = "pl.openrest.rpr.generator";

    private PredicateMethodInformationFactory predicateMethodInformationFactory;
    private PredicateMethodSpecFactory predicateMethodSpecFactory;

    private RemoteClassNamingStrategy namingStrategy;
    private TypeFileWriter typeFileWriter;
    private Reflections reflections;

    @Override
    public boolean supports(Class<?> type) {
        return type.getAnnotation(PredicateRepository.class) != null;
    }

    @Override
    public ClassName resolve(Class<?> type) {
        String className = namingStrategy.getClassName(type.getSimpleName());
        String packageName = namingStrategy.getPackageName(type.getPackage().getName());
        PredicateRepository predicateRepositoryAnn = type.getAnnotation(PredicateRepository.class);
        TypeSpec.Builder builder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        appendAnnotations(builder);
        appendPredicateMethods(builder, type);
        appendDefaultedPageableField(builder, predicateRepositoryAnn.defaultedPageable());
        typeFileWriter.write(builder.build(), packageName);
        return ClassName.get(packageName, className);
    }

    private void appendPredicateMethods(TypeSpec.Builder builder, Class<?> type) {
        for (Method method : type.getMethods()) {
            Predicate predicateAnn = method.getAnnotation(Predicate.class);
            if (predicateAnn == null)
                continue;
            PredicateMethodInformation predicateMethodInfo = predicateMethodInformationFactory.create(method, predicateAnn);
            builder.addMethod(predicateMethodSpecFactory.create(predicateMethodInfo));
            appendPredicateNameField(builder, predicateMethodInfo);
        }
    }

    private void appendPredicateNameField(TypeSpec.Builder builder, PredicateMethodInformation predicateMethodInfo) {
        builder.addField(FieldSpec
                .builder(String.class, predicateMethodInfo.getUpperCaseName(), Modifier.STATIC, Modifier.FINAL, Modifier.PRIVATE)
                .initializer("$S", predicateMethodInfo.getName()).build());
    }

    public void appendDefaultedPageableField(TypeSpec.Builder builder, boolean defaultedPageable) {
        builder.addField(FieldSpec.builder(boolean.class, "DEFAULTED_PAGEABLE", Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                .initializer("$L", defaultedPageable).build());
    }

    private void appendAnnotations(TypeSpec.Builder builder) {
        builder.addAnnotation(AnnotationSpec.builder(Generated.class).addMember("value", "$S", GENERATOR_NAME).build());
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.typeFileWriter = configuration.get("typeFileWriter");
        this.reflections = configuration.get("reflections");
        this.namingStrategy = configuration.get("defaultNamingStrategy");
        this.predicateMethodSpecFactory = new PredicateMethodSpecFactory((TypeResolver) configuration.get("defaultTypeResolver"));
        this.predicateMethodInformationFactory = new PredicateMethodInformationFactory(reflections);
    }

}
