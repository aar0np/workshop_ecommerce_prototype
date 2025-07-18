package com.datastax.tutorials.service.dataapi.entities;

import java.math.BigDecimal;
import java.util.UUID;

import com.datastax.astra.client.core.query.SortOrder;
import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;
import com.datastax.astra.client.tables.mapping.PartitionSort;

@EntityTable("order_by_id")
public class OrderTableEntity {

	@PartitionBy(0)
	@Column(name="order_id")
	private UUID orderId;
	
	@PartitionSort(position=1, order=SortOrder.ASCENDING)
	@Column(name="product_name")
	private String productName;
	
	@PartitionSort(position=2, order=SortOrder.ASCENDING)
	@Column(name="product_id")
	private String productId;

	@Column(name="order_shipping_handling")
	private BigDecimal orderShippingHandling;
	
	@Column(name="order_total")
	private BigDecimal orderTotal;
	
	@Column(name="order_status")
	private String orderStatus;
	
	@Column(name="order_subtotal")
	private BigDecimal orderSubtotal;
	
	@Column(name="order_tax")
	private BigDecimal orderTax;
	
	@Column(name="payment_method")
	private String paymentMethod;
	
	@Column(name="product_price")
	private BigDecimal productPrice;
	
	@Column(name="product_qty")
	private Integer productQty;

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public BigDecimal getOrderShippingHandling() {
		return orderShippingHandling;
	}

	public void setOrderShippingHandling(BigDecimal orderShippingHandling) {
		this.orderShippingHandling = orderShippingHandling;
	}

	public BigDecimal getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(BigDecimal orderTotal) {
		this.orderTotal = orderTotal;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public BigDecimal getOrderSubtotal() {
		return orderSubtotal;
	}

	public void setOrderSubtotal(BigDecimal orderSubtotal) {
		this.orderSubtotal = orderSubtotal;
	}

	public BigDecimal getOrderTax() {
		return orderTax;
	}

	public void setOrderTax(BigDecimal orderTax) {
		this.orderTax = orderTax;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getProductQty() {
		return productQty;
	}

	public void setProductQty(Integer productQty) {
		this.productQty = productQty;
	}
}
