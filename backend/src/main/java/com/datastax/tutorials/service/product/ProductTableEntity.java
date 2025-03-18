package com.datastax.tutorials.service.product;

import java.util.Map;
import java.util.Set;

import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;

//import lombok.Data;


@EntityTable("product")
public class ProductTableEntity {

	@PartitionBy(0)
	@Column(name="product_id")
    private String productId;
    
    @Column(name="brand")
    private String brand;
    
    @Column(name="images")
    private Set<String> images;
    
    @Column(name="linked_documents")
    private Map<String, String> linkedDocuments;
    
    @Column(name="long_desc")
    private String longDescription;
    
    @Column(name="model_number")
    private String modelNumber;
    
    @Column(name="name")
    private String name;
    
    @Column(name="product_group")
    private String productGroup;

    @Column(name="short_desc")
    private String shortDescription;
    
    @Column(name="specifications")
    private Map<String, String> specifications;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Set<String> getImages() {
		return images;
	}

	public void setImages(Set<String> images) {
		this.images = images;
	}

	public Map<String, String> getLinkedDocuments() {
		return linkedDocuments;
	}

	public void setLinkedDocuments(Map<String, String> linkedDocuments) {
		this.linkedDocuments = linkedDocuments;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public Map<String, String> getSpecifications() {
		return specifications;
	}

	public void setSpecifications(Map<String, String> specifications) {
		this.specifications = specifications;
	}


}
