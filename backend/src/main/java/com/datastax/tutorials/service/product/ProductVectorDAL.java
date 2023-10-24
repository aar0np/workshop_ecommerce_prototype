package com.datastax.tutorials.service.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class ProductVectorDAL {

	private AstraClient conn;
    private CqlSession session;

    private PreparedStatement qpPrepared;
    private PreparedStatement qvPrepared;
    
    public ProductVectorDAL() {
    	String token = System.getenv("ASTRA_DB_APP_TOKEN");
    	
        conn = AstraClient.builder()
                .withToken(token)   // credentials are mandatory
                .withDatabaseId(System.getenv("ASTRA_DB_ID"))             // identifier of the database
                .withDatabaseRegion(System.getenv("ASTRA_DB_REGION"))     // connection is different for each dc
                .enableCql()                             // as stateful, connection is not always establish
                .enableDownloadSecureConnectBundle()     // secure connect bundles can be downloaded
                .withCqlKeyspace(System.getenv("ASTRA_DB_KEYSPACE"))      // target keyspace
                .build();
        
        session = conn.cqlSession();
        
        // prepare statements
        qpPrepared = session.prepare(
    			"SELECT product_id, name, product_group, images, "
    			+ " product_vector, parent_id, category_id "
    			+ " FROM product_vectors WHERE product_id = ?");
        
        qvPrepared = session.prepare(
    			"SELECT product_id, name, product_group, images, "
    			+ "product_vector, parent_id, category_id " 
    			+ "FROM product_vectors ORDER BY product_vector ANN OF ? LIMIT 8;");
    }
    
    public Optional<ProductVector> getProductVectorById(String productId) {

		// get original product detail
		BoundStatement qpBound = qpPrepared.bind(productId);
		ResultSet rs = session.execute(qpBound);
		Row product = rs.one();
		
		ProductVector returnVal = new ProductVector(productId,
				product.getString("name"),
				product.getString("product_group"),
				product.getSet("images", String.class),
				product.getObject("product_vector"),
				product.getUuid("parent_id"),
				product.getUuid("category_id"));

		return Optional.of(returnVal);
    }
    
    public List<ProductVector> getProductsByANN(ProductVector originalProduct) {

		List<ProductVector> returnVal = new ArrayList<>();

		BoundStatement qvBound = qvPrepared.bind(originalProduct.getProductVector());
		ResultSet rsV = session.execute(qvBound);
		List<Row> ann = rsV.all();
		
		if (ann.size() > 1) {
			for (Row promo : ann) {				
				ProductVector annPromoProd = new ProductVector(
						promo.getString("product_id"),
						promo.getString("name"),
						promo.getString("product_group"),
						promo.getSet("images", String.class),
						promo.getObject("product_vector"),
						promo.getUuid("parent_id"),
						promo.getUuid("category_id"));
				returnVal.add(annPromoProd);
			}
		}
		
		return returnVal;
    }
}
