package pl.openrest.dto.mappers.generator;

import pl.openrest.generator.commons.RemoteClassNamingStrategy;

public class MapperNamingStrategy implements RemoteClassNamingStrategy {

    private static final String CLASS_NAME_FORMAT = "%sMapper";

    @Override
    public String getPackageName(String classPackage) {
        return classPackage;
    }

    @Override
    public String getClassName(String className) {
        return String.format(CLASS_NAME_FORMAT, className);
    }

}
