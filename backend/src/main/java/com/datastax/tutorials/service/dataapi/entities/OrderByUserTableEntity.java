package com.datastax.tutorials.service.dataapi.entities;

import java.math.BigDecimal;
import java.util.UUID;

import com.datastax.astra.client.core.query.SortOrder;
import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;
import com.datastax.astra.client.tables.mapping.PartitionSort;

@EntityTable("order_by_user")
public class OrderByUserTableEntity {
	
	@PartitionBy(0)
	@Column(name="user_id")
	private UUID userId;
	
	@PartitionSort(position=1, order=SortOrder.ASCENDING)
	@Column(name="order_id")
	private UUID orderId;
	
	@Column(name="order_status")
	private String orderStatus;
	
	@Column(name="order_total")
	private BigDecimal orderTotal;

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public BigDecimal getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(BigDecimal orderTotal) {
		this.orderTotal = orderTotal;
	}
}
