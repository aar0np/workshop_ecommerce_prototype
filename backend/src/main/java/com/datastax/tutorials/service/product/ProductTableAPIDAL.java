package com.datastax.tutorials.service.product;

import java.util.Optional;

import com.datastax.astra.client.DataAPIClient;
import com.datastax.astra.client.core.query.Filters;
import com.datastax.astra.client.databases.Database;
import com.datastax.astra.client.tables.Table;
import com.datastax.astra.client.tables.definition.rows.Row;

public class ProductTableAPIDAL {
	final String TOKEN = System.getenv("ASTRA_DB_APP_TOKEN");
	final String API_ENDPOINT = System.getenv("DB_API_ENDPOINT");
	final String KEYSPACE = System.getenv("ASTRA_DB_KEYSPACE");

	private Table<ProductTableEntity> productTable;
	private Table<Row> categoryTable;
	private Table<Row> priceTable;
	
	public ProductTableAPIDAL() {
		DataAPIClient client = new DataAPIClient(TOKEN);
		Database dbAPI = client.getDatabase(API_ENDPOINT, KEYSPACE);
		
		productTable = dbAPI.getTable("product", ProductTableEntity.class);
		categoryTable = dbAPI.getTable("category");
		priceTable = dbAPI.getTable("price");
	}
	
	public Optional<ProductTableEntity>  getProductById(String productId) {

		Optional<ProductTableEntity> results = productTable.findOne(Filters.eq("product_id", productId));
		return results;
	}
}
