package com.datastax.tutorials.service.dataapi;

import com.datastax.astra.client.DataAPIClient;
import com.datastax.astra.client.core.options.DataAPIClientOptions;
import com.datastax.astra.client.databases.Database;
import com.datastax.astra.client.tables.Table;
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
        if (db.tableExists("product_vectoe")) {
            return db.getTable(ProductVectorsTableEntity.class);
        } else {
            logger.info("Table 'product' does not exist, creating it ...");
            return db.createTable(ProductVectorsTableEntity.class);
        }
    }

}
