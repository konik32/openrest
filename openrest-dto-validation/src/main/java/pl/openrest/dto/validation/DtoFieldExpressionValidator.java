package pl.openrest.dto.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import pl.openrest.core.util.traverser.AnnotationFieldFilter;
import pl.openrest.core.util.traverser.ObjectGraphTraverser;
import pl.openrest.core.util.traverser.TraverserCallback;
import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.spel.evaluation.DtoEvaluationWrapper;
import pl.openrest.dto.spel.evaluation.SpelEvaluator;
import pl.openrest.dto.validation.annotation.ValidateExpression;
import pl.openrest.dto.validation.handler.ValidationContext;

public class DtoFieldExpressionValidator implements Validator {

    private final String FIELD_NAME_MESSAGE_FORMAT = "Field: %s validation expression evaluated to false";

    @Autowired
    private ValidationContext updateValidationContext;

    @Autowired
    private BeanFactory beanFactory;

    private @Setter Class<? extends Annotation> traversingAnnotation = Valid.class;

    @Override
    public boolean supports(Class<?> clazz) {
        return AnnotationUtils.isAnnotationDeclaredLocally(Dto.class, clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final TraversingStack traversingStack = new TraversingStack(updateValidationContext.getEntity());

        ObjectGraphTraverser traverser = new ObjectGraphTraverser(new ValidationCallback(traversingStack, errors),
                new AnnotationFieldFilter(traversingAnnotation), new AnnotationFieldFilter(ValidateExpression.class));

        traverser.setBeforeTraverse(new BeforeTraverseCallback(traversingStack));

        traverser.setAfterTraverse(new AfterTraverseCallback(traversingStack) {

        });

        traverser.traverse(target);
    }

    private class ValidationCallback implements TraverserCallback {
        private TraversingStack traversingStack;
        private Errors errors;

        public ValidationCallback(TraversingStack traversingStack, Errors errors) {
            this.traversingStack = traversingStack;
            this.errors = errors;
        }

        @Override
        public void doWith(Field field, Object owner, String path) throws IllegalArgumentException, IllegalAccessException {
            ValidateExpression ann = AnnotationUtils.getAnnotation(field, ValidateExpression.class);
            DtoEvaluationWrapper wrapper = new DtoEvaluationWrapper(owner, traversingStack.getEntity());
            SpelEvaluator spelEvaluator = new SpelEvaluator(wrapper, beanFactory);
            boolean isValid = spelEvaluator.evaluateAsBoolean(ann.value());
            if (!isValid) {
                String message = ann.message().isEmpty() ? String.format(FIELD_NAME_MESSAGE_FORMAT, field.getName()) : ann.message();
                errors.rejectValue(path, ValidateExpression.class.getSimpleName(), message);
            }
        }
    }

    private class BeforeTraverseCallback implements TraverserCallback {

        private TraversingStack traversingStack;

        public BeforeTraverseCallback(TraversingStack traversingContext) {
            this.traversingStack = traversingContext;
        }

        @Override
        public void doWith(Field field, Object owner, String path) throws IllegalArgumentException, IllegalAccessException {
            if (traversingStack.getEntity() == null || isCollectionLike(field.getType())) {
                traversingStack.push(null);
            } else {
                Field entityField = ReflectionUtils.findField(traversingStack.getEntity().getClass(), field.getName());
                if (entityField == null) {
                    traversingStack.push(null);
                } else {
                    ReflectionUtils.makeAccessible(entityField);
                    Object entityValue = ReflectionUtils.getField(entityField, traversingStack.getEntity());
                    traversingStack.push(entityValue);
                }
            }
        }

        private boolean isCollectionLike(Class<?> type) {
            return Iterable.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
        }
    }

    private class AfterTraverseCallback implements TraverserCallback {
        private TraversingStack traversingContext;

        public AfterTraverseCallback(TraversingStack traversingContext) {
            this.traversingContext = traversingContext;
        }

        @Override
        public void doWith(Field field, Object owner, String path) throws IllegalArgumentException, IllegalAccessException {
            traversingContext.pop();
        }
    }

    private class TraversingStack {
        private TraversingNode current;

        public TraversingStack(Object entity) {
            push(entity);
        }

        public void pop() {
            current = current.parent;
        }

        public void push(Object entity) {
            current = new TraversingNode(current, entity);
        }

        public Object getEntity() {
            return current.entity;
        }

        @RequiredArgsConstructor
        private class TraversingNode {
            private final TraversingNode parent;
            private final Object entity;

        }
    }

}
