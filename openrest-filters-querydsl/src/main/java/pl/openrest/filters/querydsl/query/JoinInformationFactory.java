package pl.openrest.filters.querydsl.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.PropertyPath;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

public class JoinInformationFactory {

    private static PathBuilderFactory builderFactory = new PathBuilderFactory();

    public static List<QJoinInformation> createJoinsInformation(String joinPath, Class<?> entityType, boolean fetch) {
        PathBuilder<?> builder = builderFactory.create(entityType);
        PropertyPath propertyPath = PropertyPath.from(joinPath, entityType);
        List<QJoinInformation> joins = new ArrayList<QJoinInformation>();
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
            joins.add(new QJoinInformation(path, fetch, collection, propertyPath.getType()));
            propertyPath = propertyPath.next();
        }
        return joins;
    }
}
