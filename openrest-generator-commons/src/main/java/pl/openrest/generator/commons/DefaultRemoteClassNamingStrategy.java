package pl.openrest.generator.commons;

public class DefaultRemoteClassNamingStrategy implements RemoteClassNamingStrategy {

    private static final String CLASS_NAME_FORMAT = "R%s";

    @Override
    public String getPackageName(String classPackage) {
        return classPackage;
    }

    @Override
    public String getClassName(String className) {
        return String.format(CLASS_NAME_FORMAT, className);
    }

}
