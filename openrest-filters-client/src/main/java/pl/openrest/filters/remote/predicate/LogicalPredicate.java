package pl.openrest.filters.remote.predicate;

import lombok.Getter;

import org.springframework.core.convert.ConversionService;

public abstract class LogicalPredicate implements Predicate {

    protected final String separator;
    protected final @Getter Predicate predicates[];

    public LogicalPredicate(String separator, Predicate... predicates) {
        this.separator = separator;
        this.predicates = predicates;
    }

    @Override
    public String toString(ConversionService conversionService) {
        String predicatesStr[] = new String[predicates.length];
        for (int i = 0; i < predicates.length; i++) {
            if (conversionService != null)
                predicatesStr[i] = predicates[i].toString(conversionService);
        }
        return String.join(separator, predicatesStr);
    }

    public static OrPredicate or(Predicate... predicates) {
        return new OrPredicate(predicates);
    }

    public static AndPredicate and(AbstractPredicate... predicates) {
        return new AndPredicate(predicates);
    }

    @Override
    public String toString() {
        String predicatesStr[] = new String[predicates.length];
        for (int i = 0; i < predicates.length; i++) {
            predicatesStr[i] = predicates[i].toString();
        }
        return String.join(",", predicatesStr);
    }

    public static class OrPredicate extends LogicalPredicate {

        private static final String SEPARATOR = ";or;";

        public OrPredicate(Predicate[] predicates) {
            super(SEPARATOR, predicates);
        }

        @Override
        public String toString() {
            return String.format("OR(%s)", super.toString());
        }
    }

    public static class AndPredicate extends LogicalPredicate {

        private static final String SEPARATOR = ";and;";

        public AndPredicate(AbstractPredicate[] predicates) {
            super(SEPARATOR, predicates);
        }

        @Override
        public String toString() {
            return String.format("AND(%s)", super.toString());
        }
    }
}
