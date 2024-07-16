package com.datastax.tutorials.service.product;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.oss.driver.api.core.data.CqlVector;

import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

/*
CREATE TABLE ecommerce.product_vectors (
	    product_id text PRIMARY KEY,
	    category_id uuid,
	    images set<text>,
	    name text,
	    parent_id uuid,
	    product_group text,
	    product_vector vector<float, 384>
	) WITH additional_write_policy = '99p'
	    AND compaction = {'class': 'org.apache.cassandra.db.compaction.UnifiedCompactionStrategy'};
	CREATE CUSTOM INDEX product_vectors_product_vector_idx ON ecommerce.product_vectors (product_vector) USING 'StorageAttachedIndex';
	*/
@Table("product_vectors")
public class ProductVectorEntity implements Serializable  {

	private static final long serialVersionUID = 8636971608491945610L;

    @PrimaryKey("product_id")
    @CassandraType(type = Name.TEXT)
	private String productId;
    
    @Column("name")
    @CassandraType(type = Name.TEXT)
	private String productName;
    
    @Column("product_group")
    @CassandraType(type = Name.TEXT)
	private	String productGroup;
    
    @Column("images")
    @CassandraType(type = CassandraType.Name.SET, typeArguments = Name.TEXT)
	private Set<String> images;
	
    @Column("product_vector")
    private CqlVector<Float> productVector;
	
    @Column("parent_id")
    @CassandraType(type = Name.UUID)
    private UUID parentId;
	
    @Column("category_id")
    @CassandraType(type = Name.UUID)
	private UUID categoryId;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public Set<String> getImages() {
		return images;
	}

	public void setImages(Set<String> images) {
		this.images = images;
	}

	public CqlVector<Float> getProductVector() {
		return productVector;
	}

	public void setProductVector(CqlVector<Float> productVector) {
		this.productVector = productVector;
	}

	public UUID getParentId() {
		return parentId;
	}

	public void setParentId(UUID parentId) {
		this.parentId = parentId;
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}
}
