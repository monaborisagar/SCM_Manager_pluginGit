package com.logilite.scm.account;

//~--- non-JDK imports --------------------------------------------------------

import java.util.Iterator;

import sonia.scm.security.CipherUtil;
import sonia.scm.store.Store;
import sonia.scm.store.StoreFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author James Christian
 */
@Singleton
public final class ScmAccountContext {

	/** Field description */
	private static final String STORE_NAME = "ScmAccount";

	// ~--- constructors
	// ---------------------------------------------------------

	/**
	 * Constructs ...
	 * 
	 * 
	 * @param storeFactory
	 */
	@Inject
	public ScmAccountContext(StoreFactory storeFactory) {

		this.store = storeFactory.getStore(ScmAccountConfiguration.class,
				STORE_NAME);

		globalConfiguration = store.get();

		if (globalConfiguration == null) {
			globalConfiguration = new ScmAccountConfiguration();
		}
	}

	// ~--- get methods
	// ----------------------------------------------------------

	/**
	 * Method description
	 * 
	 * 
	 * @param repository
	 * 
	 * @return
	 */
	public ScmAccountConfiguration getConfiguration(ScmAccount ScmAccount) {
		ScmAccountConfiguration repoConf = new ScmAccountConfiguration(
				ScmAccount);
		return globalConfiguration.merge(repoConf);
	}

	/**
	 * Method description
	 * 
	 * 
	 * @return
	 */
	public ScmAccountConfiguration getGlobalConfiguration() {
		return globalConfiguration;
	}

	// ~--- set methods
	// ----------------------------------------------------------

	/**
	 * Method description To append or create new account for remote-repository
	 * at config in xml file
	 * 
	 * @param globalConfiguration
	 */
	public void setGlobalConfiguration(
			ScmAccountConfiguration globalConfiguration) {
		/* setting id for the each <ScmAccount> POST by the request */
		Iterator<ScmAccount> decodeScmAccounts = globalConfiguration.iterator();
		CipherUtil cipher = CipherUtil.getInstance();

		while (decodeScmAccounts.hasNext()) {
			ScmAccount decodeScmAccount = decodeScmAccounts.next();
			decodeScmAccount.setId(cipher.getKeyGenerator().createKey()
					.substring(0, 8));
			/*
			 * ciphering the password, encoding at storing , decoding at
			 * retrieving
			 */
			decodeScmAccount.setPassword(cipher.encode(decodeScmAccount
					.getPassword()));
		}

		if (!this.globalConfiguration.isScmAccountAvailable()) {
			/* System.out.println("new File"); */
			this.globalConfiguration = globalConfiguration;
		} else {
			this.globalConfiguration = this.globalConfiguration
					.merge(globalConfiguration);
		}

		store.set(this.globalConfiguration);
	}

	public void deleteScmAccount(String id){
		this.globalConfiguration.remove(id);
		store.set(this.globalConfiguration);
	}
	
	// ~--- fields
	// ---------------------------------------------------------------

	/** Field description */
	private final Store<ScmAccountConfiguration> store;

	/** Field description */
	private ScmAccountConfiguration globalConfiguration;
}
