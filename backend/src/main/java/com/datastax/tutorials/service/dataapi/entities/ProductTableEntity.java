package com.datastax.tutorials.service.dataapi.entities;

import java.util.Map;
import java.util.Set;

import com.datastax.astra.client.tables.definition.columns.ColumnTypes;
import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;
import com.datastax.tutorials.service.product.Product;


@EntityTable("product")
public class ProductTableEntity {

	@PartitionBy(0)
	@Column(name="product_id", type = ColumnTypes.UUID)
    private String productId;
    
    @Column(name="brand", type = ColumnTypes.TEXT)
    private String brand;
    
    @Column(name="images", type = ColumnTypes.SET, valueType = ColumnTypes.TEXT)
    private Set<String> images;
    
    @Column(name="linked_documents", type = ColumnTypes.MAP, valueType = ColumnTypes.TEXT, keyType = ColumnTypes.TEXT)
    private Map<String, String> linkedDocuments;
    
    @Column(name="long_desc", type = ColumnTypes.TEXT)
    private String longDescription;
    
    @Column(name="model_number", type = ColumnTypes.TEXT)
    private String modelNumber;
    
    @Column(name="name", type = ColumnTypes.TEXT)
    private String name;
    
    @Column(name="product_group", type = ColumnTypes.TEXT)
    private String productGroup;

    @Column(name="short_desc", type = ColumnTypes.TEXT)
    private String shortDescription;
    
    @Column(name="specifications", type = ColumnTypes.MAP, valueType = ColumnTypes.TEXT, keyType = ColumnTypes.TEXT)
    private Map<String, String> specifications;

	public Product asProduct() {
		Product pr = new Product();
		pr.setProductId(getProductId());
		pr.setBrand(getBrand());
		pr.setImages(getImages());
		pr.setLinkedDocuments(getLinkedDocuments());
		pr.setLongDesc(getLongDescription());
		pr.setShortDesc(getShortDescription());
		pr.setSpecifications(getSpecifications());
		pr.setModelNumber(getModelNumber());
		pr.setName(getName());
		pr.setProductGroup(getProductGroup());
		return pr;
	}

	public ProductTableEntity(Product p) {
		this.productId = p.getProductId();
		this.brand = p.getBrand();
		this.images = p.getImages();
		this.linkedDocuments = p.getLinkedDocuments();
		this.longDescription = p.getLongDesc();
		this.modelNumber = p.getModelNumber();
		this.name = p.getName();
		this.productGroup = p.getProductGroup();
		this.shortDescription = p.getShortDesc();
		this.specifications = p.getSpecifications();
	}

	public ProductTableEntity() {
	}


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
