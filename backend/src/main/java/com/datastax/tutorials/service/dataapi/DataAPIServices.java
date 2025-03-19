package com.datastax.tutorials.service.dataapi;

import com.datastax.astra.client.core.vector.DataAPIVector;
import com.datastax.astra.client.databases.Database;
import com.datastax.astra.client.tables.Table;
import com.datastax.tutorials.service.dataapi.entities.ProductTableEntity;
import com.datastax.tutorials.service.dataapi.entities.ProductVectorsTableEntity;
import com.datastax.tutorials.service.dataapi.entities.ProductVectorizeTableEntity;
import com.datastax.tutorials.service.product.Product;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.datastax.astra.client.core.query.Filters.eq;

@Repository
public class DataAPIServices {

    Database db;

    @Autowired
    @Qualifier("table.product")
    Table<ProductTableEntity> productRepository;

    @Autowired
    @Qualifier("table.product_vectors")
    Table<ProductVectorsTableEntity> productVectorRepository;

    EmbeddingModel embeddingModel;

    @PostConstruct
    public void init() {
        // Compute embedding manually
        embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    }

    public DataAPIServices(Database db) {
        this.db = db;
    }

    public Optional<Product> findProductById(String productId) {
        return productRepository
                .findOne(eq("product_id", productId))
                .map(ProductTableEntity::asProduct);
    }

    public List<Product> findAllProducts() {
        return productRepository
                .findAll()
                .stream()
                .map(ProductTableEntity::asProduct)
                .toList();
    }

    public void save(Product p) {
        productRepository.insertOne(new ProductTableEntity(p));
        //saveVector(p); ??
    }

    public void saveAll(List<Product> products) {
        productRepository
                .insertMany(products.stream()
                  .map(ProductTableEntity::new)
                  .toList());
    }

    public void saveVector(Product p) {

        // Map all core features
        ProductVectorsTableEntity tve = new ProductVectorsTableEntity(p);

        // We need to compute the embeddings ourselves
        float[] embeddings = embeddingModel.embed(p.getName()).content().vector();
        tve.setProductVector(new DataAPIVector(embeddings));
        productVectorRepository.insertOne(tve);
    }
}
