package pl.openrest.generator.commons;

import java.util.HashMap;

import lombok.Getter;

@Getter
public class Configuration extends HashMap<String, Object> implements Initializable {

    private static final long serialVersionUID = -6998821046011399915L;

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) super.get(key);
    }

    @Override
    public void afterPropertiesSet() {
        doForEachDependency(new DependencyHandler() {
            @Override
            public void handle(Object object) {
                if (object instanceof Initializable)
                    ((Initializable) object).afterPropertiesSet();
            }
        });
    }

    public void initializeConfigurationAware() {
        doForEachDependency(new DependencyHandler() {
            @Override
            public void handle(Object object) {
                if (object instanceof ConfigurationAware)
                    ((ConfigurationAware) object).setConfiguration(Configuration.this);
            }
        });
    }

    private void doForEachDependency(DependencyHandler handler) {
        for (Object o : values()) {
            if (o instanceof Iterable) {
                for (Object elem : (Iterable) o) {
                    handler.handle(elem);
                }
            } else {
                handler.handle(o);
            }
        }
    }

    private interface DependencyHandler {
        void handle(Object object);
    }

}
