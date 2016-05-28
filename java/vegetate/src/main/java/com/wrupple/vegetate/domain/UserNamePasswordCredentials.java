package com.wrupple.vegetate.domain;

import java.io.Serializable;

public class UserNamePasswordCredentials implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7221029542002585847L;
	private String userName;
	private String password;

	/**
	 * @param userName
	 * @param password
	 */
	public UserNamePasswordCredentials(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	/**
	 * 
	 */
	public UserNamePasswordCredentials() {
		super();
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String toString() {
		return "UserNamePasswordCredentials [password=" + password + ", userName=" + userName + "]";
	}

}
