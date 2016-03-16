package pl.openrest.filters.domain.registry;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.support.Repositories;

import pl.openrest.filters.predicate.AbstractPredicateRepository;
import pl.openrest.filters.predicate.PredicateRepositoryFactory;
import pl.openrest.filters.predicate.annotation.PredicateRepository;

public class FilterableEntityRegistry implements ApplicationContextAware {

	private Map<Class<?>, FilterableEntityInformation> registry = new HashMap<>();

	private final Repositories repositories;
	private final PredicateRepositoryFactory predicateRepositoryFactory;

	public FilterableEntityRegistry(@NonNull Repositories repositories,
			PredicateRepositoryFactory predicateRepositoryFactory) {
		this.repositories = repositories;
		this.predicateRepositoryFactory = predicateRepositoryFactory;
	}

	private void register(Object predicateRepo) {
		PredicateRepository repoAnn = AbstractPredicateRepository
				.findAnnotation(predicateRepo);
		Class<?> entityType = repoAnn.value();

		FilterableEntityInformation entityInfo = new FilterableEntityInformation(
				entityType, predicateRepositoryFactory.create(predicateRepo));
		registry.put(entityType, entityInfo);
	}

	public FilterableEntityInformation get(Class<?> entityType) {
		return registry.get(entityType);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		for (Object predicateRepository : applicationContext
				.getBeansWithAnnotation(PredicateRepository.class).values()) {
			register(predicateRepository);
		}
	}
}
