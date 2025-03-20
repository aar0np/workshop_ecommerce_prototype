package com.datastax.tutorials.service.featured;

import java.util.ArrayList;
import java.util.List;

import com.datastax.astra.client.DataAPIClient;
import com.datastax.astra.client.core.query.Filters;
import com.datastax.astra.client.core.query.Sort;
import com.datastax.astra.client.core.query.SortOrder;
import com.datastax.astra.client.databases.Database;
import com.datastax.astra.client.tables.Table;
import com.datastax.astra.client.tables.commands.options.TableFindOptions;
import com.datastax.astra.client.tables.cursor.TableCursor;

public class FeaturedTableAPIDAL {

	final String TOKEN = System.getenv("ASTRA_DB_APP_TOKEN");
	final String API_ENDPOINT = System.getenv("DB_API_ENDPOINT");
	final String KEYSPACE = System.getenv("ASTRA_DB_KEYSPACE");

	private Table<FeaturedTableEntity> featuredProductTable;
	
	public FeaturedTableAPIDAL() {
		
		// Astra DB
		DataAPIClient client = new DataAPIClient(TOKEN);
		Database dbAPI = client.getDatabase(API_ENDPOINT, KEYSPACE);
		featuredProductTable = dbAPI.getTable("featured_product_groups", FeaturedTableEntity.class);
	}
	
	public List<FeaturedTableEntity> getFeaturedProductsById(int featuredId) {
		
		List<FeaturedTableEntity> returnVal = new ArrayList<>();
		
		Sort sort = new Sort("price", SortOrder.DESCENDING, null, null);
		TableFindOptions options = new TableFindOptions().sort(sort);
		
		TableCursor<FeaturedTableEntity, FeaturedTableEntity> results =
				featuredProductTable.find(Filters.eq("feature_id", featuredId), options);
		
		for (FeaturedTableEntity entity : results) {
			returnVal.add(entity);
		}
		
		return returnVal;
	}
}
