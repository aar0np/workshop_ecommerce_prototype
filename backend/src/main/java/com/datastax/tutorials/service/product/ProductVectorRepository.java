package com.datastax.tutorials.service.product;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.datastax.oss.driver.api.core.data.CqlVector;

public interface ProductVectorRepository extends CassandraRepository<ProductVectorEntity, String> {

	static final String VECTOR_QUERY = "SELECT product_id, name, product_group, images, "
			+ "product_vector, parent_id, category_id " 
			+ "FROM product_vectors ORDER BY product_vector ANN OF ?0 LIMIT 8;";

	@Query(VECTOR_QUERY)
	List<ProductVectorEntity> findProductsByVector(CqlVector<Float> vector);
}
