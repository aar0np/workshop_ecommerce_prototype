package com.datastax.tutorials.service.dataapi.entities;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.astra.client.tables.mapping.Column;
import com.datastax.astra.client.tables.mapping.EntityTable;
import com.datastax.astra.client.tables.mapping.PartitionBy;
import com.datastax.tutorials.service.user.AddressEntity;

@EntityTable("user")
public class UserTableEntity {
	@PartitionBy(0)
	@Column(name = "user_id")
	private UUID userId;

	@Column(name = "user_email")
	private String userEmail;

	@Column(name = "picture_url")
	private String pictureUrl;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	private String locale;
	private List<AddressEntity> addresses;
	
	@Column(name = "session_id")
	private String sessionId;
	private String password;
	
	@Column(name = "password_timestamp")
	private Date passwordTimestamp;

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

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public List<AddressEntity> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressEntity> addresses) {
		this.addresses = addresses;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getPasswordTimestamp() {
		return passwordTimestamp;
	}

	public void setPasswordTimestamp(Date passwordTimestamp) {
		this.passwordTimestamp = passwordTimestamp;
	}
}
