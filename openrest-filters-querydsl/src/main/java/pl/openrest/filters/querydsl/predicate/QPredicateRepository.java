package pl.openrest.filters.querydsl.predicate;

import java.util.ArrayList;
import java.util.List;

import pl.openrest.filters.predicate.AbstractPredicateRepository;
import pl.openrest.filters.query.annotation.Join;
import pl.openrest.filters.query.registry.JoinInformationFactory;
import pl.openrest.filters.query.registry.QJoinInformation;

public class QPredicateRepository extends AbstractPredicateRepository {

    public QPredicateRepository(Object predicateRepository) {
        super(predicateRepository);
    }

    @Override
    protected List<QJoinInformation> getJoins(Join[] joins, Class<?> entityType) {
        List<QJoinInformation> joinsInfo = new ArrayList<QJoinInformation>(joins.length);
        for (Join join : joins) {
            joinsInfo.addAll(JoinInformationFactory.createJoinsInformation(join.value(), entityType, join.fetch()));
        }
        return joinsInfo;
    }

}
