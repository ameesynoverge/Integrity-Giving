package com.integritygiving.model;

public class Login {

	private String userName;
	private String password;
	private String Token;
	private Boolean ProductPurchase;
	
	public Boolean getProductPurchase() {
		return ProductPurchase;
	}
	public void setProductPurchase(Boolean productPurchase) {
		ProductPurchase = productPurchase;
	}
	public String getToken() {
		return Token;
	}
	public void setToken(String token) {
		Token = token;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}

