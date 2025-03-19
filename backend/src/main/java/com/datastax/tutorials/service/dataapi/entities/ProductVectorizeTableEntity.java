package com.datastax.tutorials.service.dataapi.entities;

import com.datastax.astra.client.core.vector.DataAPIVector;
import com.datastax.astra.client.core.vector.SimilarityMetric;
import com.datastax.astra.client.tables.definition.columns.ColumnTypes;
import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;

import java.util.Set;
import java.util.UUID;

@EntityTable("product_vectorize")
public class ProductVectorizeTableEntity {

	@PartitionBy(0)
	@Column(name="product_id", type = ColumnTypes.UUID)
	private String productId;

	@Column(name="images", type = ColumnTypes.SET, valueType = ColumnTypes.TEXT)
	private Set<String> images;

	@Column(name="name",type = ColumnTypes.TEXT)
	private String name;

	@Column(name="product_group", type = ColumnTypes.TEXT)
	private	String group;

	@Column(name="product_vector", type = ColumnTypes.VECTOR, dimension = 384, metric = SimilarityMetric.COSINE)
	private DataAPIVector productVector;

	@Column(name="parent_id", type = ColumnTypes.UUID)
	private UUID parentId;

	@Column(name="category_id", type = ColumnTypes.UUID)
	private UUID categoryId;

	/**
	 * Constructor
	 */
	public ProductVectorizeTableEntity() {}

	/**
	 * Gets productId
	 *
	 * @return value of productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * Set value for productId
	 *
	 * @param productId new value for productId
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * Gets images
	 *
	 * @return value of images
	 */
	public Set<String> getImages() {
		return images;
	}

	/**
	 * Set value for images
	 *
	 * @param images new value for images
	 */
	public void setImages(Set<String> images) {
		this.images = images;
	}

	/**
	 * Gets name
	 *
	 * @return value of name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set value for name
	 *
	 * @param name new value for name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets group
	 *
	 * @return value of group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Set value for group
	 *
	 * @param group new value for group
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Gets productVector
	 *
	 * @return value of productVector
	 */
	public DataAPIVector getProductVector() {
		return productVector;
	}

	/**
	 * Set value for productVector
	 *
	 * @param productVector new value for productVector
	 */
	public void setProductVector(DataAPIVector productVector) {
		this.productVector = productVector;
	}

	/**
	 * Gets parentId
	 *
	 * @return value of parentId
	 */
	public UUID getParentId() {
		return parentId;
	}

	/**
	 * Set value for parentId
	 *
	 * @param parentId new value for parentId
	 */
	public void setParentId(UUID parentId) {
		this.parentId = parentId;
	}

	/**
	 * Gets categoryId
	 *
	 * @return value of categoryId
	 */
	public UUID getCategoryId() {
		return categoryId;
	}

	/**
	 * Set value for categoryId
	 *
	 * @param categoryId new value for categoryId
	 */
	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}
}
