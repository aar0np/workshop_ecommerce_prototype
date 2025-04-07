package com.datastax.tutorials.service.dataapi;

import com.datastax.astra.client.DataAPIClient;
import com.datastax.astra.client.core.options.DataAPIClientOptions;
import com.datastax.astra.client.core.vector.SimilarityMetric;
import com.datastax.astra.client.core.vectorize.VectorServiceOptions;
import com.datastax.astra.client.databases.Database;
import com.datastax.astra.client.tables.Table;
import com.datastax.astra.client.tables.definition.TableDefinition;
import com.datastax.astra.client.tables.definition.columns.ColumnDefinitionVector;
import com.datastax.astra.client.tables.definition.indexes.TableVectorIndexDefinition;
import com.datastax.astra.client.tables.definition.rows.Row;
import com.datastax.tutorials.service.dataapi.entities.CartProductTableEntity;
import com.datastax.tutorials.service.dataapi.entities.FeaturedTableEntity;
import com.datastax.tutorials.service.dataapi.entities.PriceTableEntity;
import com.datastax.tutorials.service.dataapi.entities.ProductTableEntity;
import com.datastax.tutorials.service.dataapi.entities.ProductVectorsTableEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataAPIConfiguration {

    static Logger logger = LoggerFactory.getLogger(DataAPIConfiguration.class);

    @Value("${astra.api.application-token}")
    private String astraToken;

    @Value("${astra.api.database-endpoint}")
    private String astraDbApiEndpoint;

    @Value("${astra.cql.driver-config.basic.session-keyspace}")
    private String astraDbKeyspace;

    @Bean
    public DataAPIClient dataAPIClient() {
        logger.info("Initializing Data API Client...");
        DataAPIClientOptions options = new DataAPIClientOptions().logRequests();
        return new DataAPIClient(astraToken, options);
    }

    @Bean
    public Database database(DataAPIClient client) {
        logger.info("Initializing database ...");
        // astraDbKeyspace can be omitted if default value : default_keyspace is used
        return client.getDatabase(astraDbApiEndpoint, astraDbKeyspace);
    }

    @Bean("table.product")
    public Table<ProductTableEntity> tableProduct(Database db) {
        if (db.tableExists("product")) {
            return db.getTable(ProductTableEntity.class);
        } else {
            logger.info("Table 'product' does not exist, creating it ...");
            return db.createTable(ProductTableEntity.class);
        }
    }

    @Bean("table.product_vectors")
    public Table<ProductVectorsTableEntity> tableProductVectors(Database db) {
        if (db.tableExists("product_vectors")) {
            return db.getTable(ProductVectorsTableEntity.class);
        } else {
            logger.info("Table 'product_vectors' does not exist, creating it ...");
            return db.createTable(ProductVectorsTableEntity.class);
        }
    }
    
    @Bean("table.product_vectorize")
    public Table<Row> tableProductVectorize(Database db) {
        if (db.tableExists("product_vectorize")) {
            return db.getTable("product_vectorize");
        } else {
            logger.info("Table 'product_vectorize' does not exist, creating it ...");
            // Table Creation
            TableDefinition tableDefinition = new TableDefinition()
                            // Define all of the columns in the table
                            .addColumnText("product_id")
                            .addColumnText("name")
                            .addColumnText("description")
                            .addColumnVector(
                                    "product_vector",
                                    new ColumnDefinitionVector()
                                            .dimension(1024)
                                            .metric(SimilarityMetric.COSINE)
                                            .service(new VectorServiceOptions()
                                                    .provider("nvidia").modelName("NV-Embed-QA")))
                            .addPartitionBy("product_id");
            db.createTable("product_vectorize", tableDefinition);

            // Index creation
            Table<Row> tableVectorize = db.getTable("product_vectorize");
            tableVectorize.createVectorIndex("idx_product_vectorize", new TableVectorIndexDefinition()
                    .column("product_vector")
                    .metric(SimilarityMetric.COSINE));
            return tableVectorize;
        }
    }

    @Bean("table.featured_product_groups")
    public Table<FeaturedTableEntity> tableFeaturedProductGroups(Database db) {
        if (db.tableExists("featured_product_groups")) {
            return db.getTable(FeaturedTableEntity.class);
        } else {
            logger.info("Table 'product' does not exist, creating it ...");
            return db.createTable(FeaturedTableEntity.class);
        }
    }
    
    @Bean("table.price")
    public Table<PriceTableEntity> tablePrice(Database db) {
		if (db.tableExists("price")) {
			return db.getTable(PriceTableEntity.class);
		} else {
			logger.info("Table 'price' does not exist, creating it ...");
			return db.createTable(PriceTableEntity.class);
		}
	}
    
    @Bean("table.cart_products")
    public Table<CartProductTableEntity> tableCartProduct(Database db) {
    	if (db.tableExists("cart_products")) {
			return db.getTable(CartProductTableEntity.class);
		} else {
			logger.info("Table 'cart_products' does not exist, creating it ...");
			return db.createTable(CartProductTableEntity.class);
		}
    }
}
