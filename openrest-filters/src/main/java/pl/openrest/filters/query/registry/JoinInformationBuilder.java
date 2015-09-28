package pl.openrest.filters.query.registry;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.PropertyPath;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

public class JoinInformationBuilder {

    private static PathBuilderFactory builderFactory;

    public static List<JoinInformation> getJoinsInformation(String joinPath, Class<?> entityType, boolean fetch) {
        PathBuilder<?> builder = builderFactory.create(entityType);
        PropertyPath propertyPath = PropertyPath.from(joinPath, entityType);
        List<JoinInformation> joins = new ArrayList<JoinInformation>();
        String dotPath = "";
        while (propertyPath != null) {
            Path<?> path = null;
            boolean collection = false;
            dotPath += dotPath.isEmpty() ? propertyPath.getSegment() : "." + propertyPath.getSegment();
            if (propertyPath.isCollection()) {
                path = builder.getCollection(dotPath, propertyPath.getType());
                collection = true;
            } else {
                path = builder.get(dotPath, propertyPath.getType());
            }
            joins.add(new JoinInformation(path, collection, fetch, propertyPath.getType()));
            propertyPath = propertyPath.next();
        }
        return joins;
    }
}
