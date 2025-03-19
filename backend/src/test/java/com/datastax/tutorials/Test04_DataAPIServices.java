package com.datastax.tutorials;

import com.datastax.astra.client.core.vector.DataAPIVector;
import com.datastax.astra.client.tables.Table;
import com.datastax.tutorials.service.dataapi.DataAPIServices;
import com.datastax.tutorials.service.dataapi.entities.ProductTableEntity;
import com.datastax.tutorials.service.dataapi.entities.ProductVectorsTableEntity;
import com.datastax.tutorials.service.product.Product;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Test04_DataAPIServices {

    @Autowired
    DataAPIServices dataAPIServices;

    @Autowired
    @Qualifier("table.product")
    Table<ProductTableEntity> productRepository;

    @Test
    public void should_list_products() {
        productRepository.findAll()
                .stream()
                .map(ProductTableEntity::getName)
                .forEach(System.out::println);
    }

    @Test
    public void should_create_product() {
        Product p = new Product();
        p.setProductId("1234");
        p.setName("My Product");
        p.setBrand("Hermes");
        p.setModelNumber("234");
        dataAPIServices.save(p);
    }

    @Test
    public void should_embedded_string() {
        EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();
        System.out.println(model.embed("Hello world").content().vector().length);

    }

    @Test
    public void should_insert_product_with_vector() {
        Product p = new Product();
        p.setProductId("1234");
        p.setName("this is a super long names to test the vector");
        p.setBrand("Hermes");
        p.setModelNumber("234");
        dataAPIServices.saveVector(p);
    }

}
