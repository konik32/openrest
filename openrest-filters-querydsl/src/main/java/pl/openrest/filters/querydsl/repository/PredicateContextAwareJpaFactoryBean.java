package pl.openrest.filters.querydsl.repository;

import static org.springframework.data.querydsl.QueryDslUtils.QUERY_DSL_PRESENT;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class PredicateContextAwareJpaFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable> extends
        JpaRepositoryFactoryBean<R, T, I> {
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

        return new PredicateContextAwareJpaFactory(entityManager);
    }

    private static class PredicateContextAwareJpaFactory<T, I extends Serializable> extends JpaRepositoryFactory {

        public PredicateContextAwareJpaFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata,
                EntityManager entityManager) {
            Class<?> repositoryInterface = metadata.getRepositoryInterface();
            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

            SimpleJpaRepository<?, ?> repo = isQueryDslExecutor(repositoryInterface) ? new PredicateContextQueryDslRepositoryImpl(
                    entityInformation, entityManager) : new SimpleJpaRepository(entityInformation, entityManager);

            return repo;
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {

            if (isQueryDslExecutor(metadata.getRepositoryInterface())) {
                return PredicateContextQueryDslRepositoryImpl.class;
            } else {
                return SimpleJpaRepository.class;
            }
        }

        private boolean isQueryDslExecutor(Class<?> repositoryInterface) {

            return QUERY_DSL_PRESENT && QueryDslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
        }
    }
}
