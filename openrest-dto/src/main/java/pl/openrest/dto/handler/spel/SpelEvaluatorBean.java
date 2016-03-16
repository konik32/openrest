package pl.openrest.dto.handler.spel;

import java.lang.reflect.Field;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import pl.openrest.core.util.traverser.AnnotationFieldFilter;
import pl.openrest.core.util.traverser.ObjectGraphTraverser;
import pl.openrest.core.util.traverser.TraverserCallback;
import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.handler.BeforeCreateMappingHandler;
import pl.openrest.dto.handler.BeforeUpdateMappingHandler;

/**
 * This bean evaluates {@link Dto}'s field annotated with {@link Value}. Nested {@link Dto}s and parametarized {@link Iterable} of
 * {@link Dto} are also evaluated.
 * 
 * @author Szymon Konicki
 *
 */
public class SpelEvaluatorBean implements BeforeCreateMappingHandler, BeforeUpdateMappingHandler {

    private final BeanFactory beanFactory;

    public SpelEvaluatorBean(BeanFactory beanFactory) {
        Assert.notNull(beanFactory);
        this.beanFactory = beanFactory;
    }

    private void evaluate(final DtoEvaluationWrapper wrapper) {
        ObjectGraphTraverser traverser = new ObjectGraphTraverser(new TraverserCallback() {
            @Override
            public void doWith(Field field, Object owner, String path) throws IllegalArgumentException, IllegalAccessException {
                final SpelEvaluator spelEvaluator = new SpelEvaluator(new DtoEvaluationWrapper(owner, null), beanFactory);
                Object value = spelEvaluator.evaluate(field);
                field.set(owner, value);
            }
        }, new AnnotationFieldFilter(Evaluate.class), new AnnotationFieldFilter(Value.class));
        traverser.traverse(wrapper.getDto());
    }

    @Override
    public void handle(Object dto) {
        handle(dto, null);
    }

    @Override
    public void handle(Object dto, Object entity) {
        DtoEvaluationWrapper wrapper = new DtoEvaluationWrapper(dto, entity);
        evaluate(wrapper);
    }

}
