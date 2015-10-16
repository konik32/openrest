package pl.openrest.filters.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import pl.openrest.filters.remote.predicate.ParameterSerializer;
import pl.openrest.filters.remote.predicate.Predicate;
import pl.openrest.filters.remote.predicate.SearchPredicate;

@Getter
@ToString
@EqualsAndHashCode
public class GetRequestInformation {

    private final String path;
    private final Map<String, List<String>> parameters;

    private GetRequestInformation(String path, Map<String, List<String>> parameters) {
        this.path = path;
        this.parameters = parameters;
    }

    public String getParametersString() {
        List<String> params = new LinkedList<String>();
        for (Entry<String, List<String>> entries : parameters.entrySet()) {
            for (String value : entries.getValue()) {
                params.add(value == null || value.isEmpty() ? entries.getKey() : String.format("%s=%s", entries.getKey(), value));
            }
        }
        return String.join("&", params);
    }

    public String getPathWithParameters() {
        String params = getParametersString();
        return params.isEmpty() ? path : path + "?" + params;
    }

    public static class GetRequestInformationBuilder {

        private static final String BOTH_ID_AND_SEARCH_EXCEPTION_MSG = "You cannot search by both id and search predicate/query method";

        private @Setter static ParameterSerializer defualtSerializer;

        private final ParameterSerializer serializer;

        private final String path;
        private List<String> filters = new LinkedList<String>();
        private Map<String, List<String>> customParameters = new HashMap<>();
        private String searchPath;
        private String id;
        private Integer page;
        private Integer size;
        private String projection;

        public GetRequestInformationBuilder(String path) {
            if (defualtSerializer == null)
                throw new IllegalStateException("You should set default serializer");
            this.serializer = defualtSerializer;
            this.path = path;
        }

        public GetRequestInformationBuilder(ParameterSerializer serializer, String path) {
            this.serializer = serializer;
            this.path = path;
        }

        public GetRequestInformationBuilder filter(Predicate... predicates) {
            for (Predicate predicate : predicates)
                filters.add(predicate.toString(serializer));
            return this;
        }

        public GetRequestInformationBuilder search(@NonNull SearchPredicate predicate) {
            search(predicate.toString(serializer));
            return this;
        }

        public GetRequestInformationBuilder search(@NonNull String queryMethod) {
            searchPath = queryMethod;
            return this;
        }

        public GetRequestInformationBuilder parameter(@NonNull String parameterName, Object... values) {
            List<String> valuesStrs = new LinkedList<>();
            for (Object value : values) {
                valuesStrs.add(serializer.serialize(value));
            }
            customParameters.put(parameterName, valuesStrs);
            return this;
        }

        public GetRequestInformationBuilder page(int page) {
            this.page = page;
            return this;
        }

        public GetRequestInformationBuilder size(int size) {
            this.size = size;
            return this;
        }

        public GetRequestInformationBuilder id(@NonNull String id) {
            this.id = id;
            return this;
        }

        public GetRequestInformationBuilder id(@NonNull Object id) {
            return id(serializer.serialize(id));
        }

        public GetRequestInformationBuilder projection(@NonNull String projection) {
            this.projection = projection;
            return this;
        }

        private Map<String, List<String>> getParameters() {
            Map<String, List<String>> parameters = new HashMap<>();
            parameters.put("orest", Collections.singletonList(""));
            parameters.putAll(customParameters);
            if (!filters.isEmpty())
                parameters.put("filter", filters);
            if (page != null)
                parameters.put("page", Collections.singletonList(page.toString()));
            if (size != null)
                parameters.put("size", Collections.singletonList(size.toString()));
            if (projection != null)
                parameters.put("projection", Collections.singletonList(projection));
            return Collections.unmodifiableMap(parameters);
        }

        private String getPath() {
            return searchPath != null ? String.format("%s/search/%s", path, searchPath) : id != null ? String.format("%s/%s", path, id)
                    : path;
        }

        public GetRequestInformation build() {
            if (id != null && searchPath != null)
                throw new IllegalStateException(BOTH_ID_AND_SEARCH_EXCEPTION_MSG);
            return new GetRequestInformation(getPath(), getParameters());
        }
    }
}
