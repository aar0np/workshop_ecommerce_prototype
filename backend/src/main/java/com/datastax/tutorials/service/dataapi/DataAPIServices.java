package com.datastax.tutorials.service.dataapi;

import com.datastax.astra.client.core.query.Filter;
import com.datastax.astra.client.core.query.Filters;
import com.datastax.astra.client.core.query.Projection;
import com.datastax.astra.client.core.query.Sort;
import com.datastax.astra.client.core.query.SortOrder;
import com.datastax.astra.client.core.vector.DataAPIVector;
import com.datastax.astra.client.databases.Database;
import com.datastax.astra.client.tables.Table;
import com.datastax.astra.client.tables.commands.options.TableFindOneOptions;
import com.datastax.astra.client.tables.commands.options.TableFindOptions;
import com.datastax.astra.client.tables.cursor.TableFindCursor;
import com.datastax.astra.client.tables.definition.rows.Row;
import com.datastax.tutorials.service.dataapi.entities.CartProductTableEntity;
import com.datastax.tutorials.service.dataapi.entities.CategoryTableEntity;
import com.datastax.tutorials.service.dataapi.entities.FeaturedTableEntity;
import com.datastax.tutorials.service.dataapi.entities.PriceTableEntity;
import com.datastax.tutorials.service.dataapi.entities.ProductTableEntity;
import com.datastax.tutorials.service.dataapi.entities.ProductVectorsTableEntity;
import com.datastax.tutorials.service.dataapi.entities.UserTableEntity;
import com.datastax.tutorials.service.product.Product;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    @Qualifier("table.product_vectorize")
    Table<Row> productVectorizeRepository;

    @Autowired
    @Qualifier("table.featured_product_groups")
    Table<FeaturedTableEntity> featuredProductRepository;
    
    @Autowired
    @Qualifier("table.price")
    Table<PriceTableEntity> priceRepository;
    
    @Autowired
    @Qualifier("table.cart_products")
    Table<CartProductTableEntity> cartProductRepository;
    
    @Autowired
    @Qualifier("table.category")
    Table<CategoryTableEntity> categoryRepository;
    
    @Autowired
    @Qualifier("table.user")
    Table<UserTableEntity> userRepository;

//    @Autowired
//    @Qualifier("table.user_by_email")
//    Table<UserByEmailTableEntity> userByEmailRepository;
    
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
        // <---
        productVectorRepository.insertOne(tve);
    }

    public void saveProductVectorize(String id, String name, String description) {
        productVectorizeRepository.insertOne(new Row()
                .addText("product_id", id)
                .addText("name", name)
                .addText("description", description)
                .addVectorize("product_vector", description));
    }

    public List<ProductVectorsTableEntity>  vectorSearchProducts(String query)  {
        // Compute the query Embeddings
        float[] queryEmbeddings =
                embeddingModel.embed(query).content().vector();

        // Search the vectors
        return  productVectorRepository.find(null, new TableFindOptions()
                .sort(Sort.vector("product_vector", queryEmbeddings))
                .includeSimilarity(true)
                .limit(10)).toList();
    }

    public List<Row> vectorizeSearchProducts(String description) {
        return productVectorizeRepository.find(null, new TableFindOptions()
                .sort(Sort.vectorize("product_vector", description))
                .includeSimilarity(true)
                .limit(10)).toList();
    }
    
    public List<ProductVectorsTableEntity> findProductsByVector(DataAPIVector vector) {
    	
    	return productVectorRepository.find(null, new TableFindOptions()
						.sort(Sort.vector("product_vector", vector))
						.includeSimilarity(true)
						.limit(8)).toList();
    }
    
    public Optional<ProductVectorsTableEntity> findProductVectorById(String productId) {
		return productVectorRepository.findOne(eq("product_id", productId));
	}  
    
	public List<FeaturedTableEntity> getFeaturedProductsById(int featuredId) {
		
		//Sort sort = new Sort("price", SortOrder.DESCENDING);
		Sort sort = Sort.descending("price");
		
		TableFindOptions options = new TableFindOptions().sort(sort);
		
		TableFindCursor<FeaturedTableEntity, FeaturedTableEntity> results =
				featuredProductRepository.find(Filters.eq("feature_id", featuredId), options);
				
		return results.toList();
	}
	
	public List<PriceTableEntity> getAllPricesByProductId(String productId) {
		
		List<PriceTableEntity> returnVal = new ArrayList<>();
		
		//Sort sort = new Sort("value", SortOrder.DESCENDING, null, null);
		Sort sort = Sort.descending("value");
		TableFindOptions options = new TableFindOptions().sort(sort);
		
		TableFindCursor<PriceTableEntity, PriceTableEntity> results =
				priceRepository.find(Filters.eq("product_id", productId), options);
		
		for (PriceTableEntity entity : results) {
			returnVal.add(entity);
		}
		
		return returnVal;
	}
	
	public Optional<PriceTableEntity> getPriceByProductIdAndStoreId(String productId, String storeId) {
		Filter filter  = new Filter(Map.of("product_id", productId, "store_id", storeId));
		
		return priceRepository.findOne(filter);
	}
	
	public List<CartProductTableEntity> getCartProductByCartId(UUID cartId) {
		Filter filter  = new Filter(Map.of("cart_id", cartId));
		
		TableFindCursor<CartProductTableEntity, CartProductTableEntity> results =
				cartProductRepository.find(filter);
		List<CartProductTableEntity> returnVal = new ArrayList<>();
		
		for (CartProductTableEntity entity : results) {
			returnVal.add(entity);
		}
		
		return returnVal;
	}
	
	public void saveCartProduct(CartProductTableEntity cartProduct) {
		cartProductRepository.insertOne(cartProduct);
	}
	
	public void deleteCartProduct(UUID cartId, String productId, Instant productTimestamp) {
		Filter filter = new Filter(Map.of("cart_id", cartId, "product_timestamp", productTimestamp, "product_id", productId));
		
		cartProductRepository.deleteOne(filter);
	}
	
	public List<CategoryTableEntity> getCategoriesByParentId(UUID parentId) {
		Filter filter  = new Filter(Map.of("parent_id", parentId));
		
		return categoryRepository.find(filter).toList();
	}
	
	public List<CategoryTableEntity> getCategoriesByParentIdAndCategoryId(UUID parentId, UUID categoryId) {
		Filter filter  = new Filter(Map.of("parent_id", parentId, "category_id", categoryId));
		
		return categoryRepository.find(filter).toList();
	}
	
//	public Optional<UserByEmailTableEntity> getUserByEmail(String email) {
//		Filter filter  = new Filter(Map.of("user_email", email));
//		
//		return userByEmailRepository.findOne(filter);
//	}
	
//	public void saveUserByEmail(UserByEmailTableEntity user) {
//		userByEmailRepository.insertOne(user);
//	}
	
	public void saveUser(UserTableEntity user) {
		userRepository.insertOne(user);
	}
	
	public Optional<UserTableEntity> getUserById(UUID userId) {
		Filter filter  = new Filter(Map.of("user_id", userId));
		Projection projection = new Projection("addresses",false);
		TableFindOneOptions options = new TableFindOneOptions()
				.projection(projection);
		return userRepository.findOne(filter, options);
	}
	
	public Optional<UserTableEntity> getUserByEmail(String email) {
		Filter filter  = new Filter(Map.of("user_email", email));
		Projection projection = new Projection("addresses",false);
		TableFindOneOptions options = new TableFindOneOptions()
				.projection(projection);		
		return userRepository.findOne(filter,options);
	}
	
//	public void deleteUserByEmail(String email) {
//		Filter filter  = new Filter(Map.of("user_email", email));
//		
//		userByEmailRepository.deleteOne(filter);
//	}
}
