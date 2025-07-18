package com.datastax.tutorials.service.dataapi.entities;

import java.util.UUID;

import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;

@EntityTable("user_by_email")
public class UserByEmailTableEntity {

	@PartitionBy(0)
	@Column(name = "user_id")
	private UUID userId;

	@Column(name = "user_email")
	private String userEmail;

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
