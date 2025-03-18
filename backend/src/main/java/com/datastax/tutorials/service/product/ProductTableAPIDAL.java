package com.datastax.tutorials.service.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.datastax.astra.client.DataAPIClient;
import com.datastax.astra.client.core.query.Filter;
import com.datastax.astra.client.core.query.Filters;
import com.datastax.astra.client.databases.Database;
import com.datastax.astra.client.tables.Table;
import com.datastax.astra.client.tables.definition.rows.Row;

public class ProductTableAPIDAL {
	final String TOKEN = System.getenv("ASTRA_DB_APP_TOKEN");
	final String API_ENDPOINT = System.getenv("DB_API_ENDPOINT");
	final String KEYSPACE = System.getenv("ASTRA_DB_KEYSPACE");

	Table<ProductTableEntity> productTable;
	Table<Row> categoryTable;
	Table<Row> priceTable;
	Table<Row> featuredProductTable;
	
	public ProductTableAPIDAL() {
		DataAPIClient client = new DataAPIClient(TOKEN);
		Database dbAPI = client.getDatabase(API_ENDPOINT, KEYSPACE);
		
		productTable = dbAPI.getTable("product", ProductTableEntity.class);
		categoryTable = dbAPI.getTable("category");
		priceTable = dbAPI.getTable("price");
		featuredProductTable = dbAPI.getTable("featured_product_groups");
	}
	
	public Optional<ProductTableEntity>  getProductById(String productId) {
//		Filter filter = new Filter()
//				.where("product_id").isEqualsTo(productId);
		Optional<ProductTableEntity> results = productTable.findOne(Filters.eq("product_id", productId));
		
		
		//ProductEntity product = mapRowToProductEntity(results);	
		//return Optional.ofNullable(product);
		return results;
	}
	
	private ProductEntity mapRowToProductEntity(Optional<Row> rows) {

		if (rows.isPresent()) {
			Row row = rows.get();

			ProductEntity product = new ProductEntity();
			product.setProductId(row.getText("product_id"));
			product.setName(row.getText("product_name"));
			product.setLongDescription(row.getText("long_desc"));
			product.setShortDescription(row.getText("short_desc"));
			product.setProductGroup(row.getText("product_group"));
			product.setBrand(row.getText("brand"));
			product.setModelNumber(row.getText("model_number"));
			
			//product.setImages(row.get("product_images"));
			// LinkedHashMap$Entry - Key: "images" Value: "ls534.png"
			
			LinkedHashMap<String,String> imageObj = (LinkedHashMap<String,String>) row.get("images");

			if (imageObj != null) {
				Set<String> images = new HashSet<String>();
				for (String key : imageObj.keySet()) {
					images.add(imageObj.get(key));
				}
				product.setImages(images);
			}
			
			//product.setSpecifications(row.get("product_specifications"));
			// LinkedHashMap$Entry - Key: "specifications", LinkedHashMap Value: {Key: "color" Value: "blue", Key: "size" Value: "large"}
			return product;
		}
		return null;
	}
}
