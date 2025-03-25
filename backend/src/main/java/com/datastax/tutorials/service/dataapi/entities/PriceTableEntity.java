package com.datastax.tutorials.service.dataapi.entities;

import java.math.BigDecimal;

import com.datastax.astra.client.core.query.SortOrder;
import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;
import com.datastax.astra.client.tables.mapping.PartitionSort;

@EntityTable("price")
public class PriceTableEntity {

	@PartitionBy(0)
	@Column(name="product_id")
	private String productId;
	
	@PartitionSort(position=1, order=SortOrder.ASCENDING)
	@Column(name="store_id")
	private String storeId;
	
	@Column(name="value")
	private BigDecimal value;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
