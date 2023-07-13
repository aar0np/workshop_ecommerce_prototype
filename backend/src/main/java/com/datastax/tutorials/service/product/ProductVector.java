package com.datastax.tutorials.service.product;

import java.util.Set;
import java.util.UUID;

public class ProductVector {

	private String productId;
	private String productName;
	private	String productGroup;
	private Set<String> images;
	private Object productVector;
	private UUID parentId;
	private UUID categoryId;
	
	public ProductVector(String productId, String productName,
    		String productGroup, Set<String> images, Object productVector, UUID parentId, UUID categoryId) {
		
		this.productId = productId;
		this.productName = productName;
		this.productGroup = productGroup;
		this.images = images;
		this.productVector = productVector;
		this.parentId = parentId;
		this.categoryId = categoryId;
    }

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

	public Object getProductVector() {
		return productVector;
	}

	public void setProductVector(Object productVector) {
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
