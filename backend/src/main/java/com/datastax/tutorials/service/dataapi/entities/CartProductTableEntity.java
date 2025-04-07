package com.datastax.tutorials.service.dataapi.entities;

import java.time.Instant;
import java.util.UUID;

import com.datastax.astra.client.core.query.SortOrder;
import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;
import com.datastax.astra.client.tables.mapping.PartitionSort;

@EntityTable("cart_products")
public class CartProductTableEntity {

	@PartitionBy(0)
	@Column(name="cart_id")
	private UUID cartId;
	
	@PartitionSort(position=1, order=SortOrder.DESCENDING)
	@Column(name="product_timestamp")
	private Instant productTimestamp;
	
	@PartitionSort(position=2, order=SortOrder.ASCENDING)
	@Column(name="product_id")
	private String productId;
	
	@Column(name="product_name")
	private String productName;
	
	@Column(name="product_description")
	private String productDescription;
	
	@Column(name="quantity")
	private int quantity;

	public UUID getCartId() {
		return cartId;
	}

	public void setCartId(UUID cartId) {
		this.cartId = cartId;
	}

	public Instant getProductTimestamp() {
		return productTimestamp;
	}

	public void setProductTimestamp(Instant productTimestamp) {
		this.productTimestamp = productTimestamp;
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

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
