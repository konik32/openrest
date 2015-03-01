package orest.repository;


import orest.model.User;
import orest.model.projection.UserProjection;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
@RepositoryRestResource(excerptProjection=UserProjection.class)
public interface UserRepository extends PagingAndSortingRepository<User, Long>, PredicateContextQueryDslRepository<User> {

}
