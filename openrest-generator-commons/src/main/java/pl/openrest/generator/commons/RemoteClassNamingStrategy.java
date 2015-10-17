package pl.openrest.generator.commons;

public interface RemoteClassNamingStrategy {

    String getPackageName(String classPackage);

    String getClassName(String className);
}
