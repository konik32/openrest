package orest.repository;

import orest.model.Product;
import orest.model.projection.ProductProjection;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection=ProductProjection.class)
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, PredicateContextQueryDslRepository<Product> {
	
}
