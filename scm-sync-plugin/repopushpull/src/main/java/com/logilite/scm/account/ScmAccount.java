package com.logilite.scm.account;

import com.google.common.base.Objects;

public class ScmAccount {

	ScmAccount(){
		
	}
	
	public ScmAccount(String id,String accountName,String username, String password) {
		super();
		this.id = id;
		this.accountName=accountName;
		this.username = username;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// ~--- methods
	// --------------------------------------------------------------

	/**
	 * Method description
	 * 
	 * 
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final ScmAccount other = (ScmAccount) obj;

		return Objects.equal(accountName, other.accountName)
				&&Objects.equal(username, other.username)
				&& Objects.equal(password, other.password);
	}

	/**
	 * Method description
	 * 
	 * 
	 * @return
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(accountName,username, password);
	}

	/**
	 * Method description
	 * 
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		// J-
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("accountname",accountName)
				.add("username", username)
				.add("password", password).toString();
		// J+
	}

	private String id;
	private String accountName;
	private String username;
	private String password;
}
