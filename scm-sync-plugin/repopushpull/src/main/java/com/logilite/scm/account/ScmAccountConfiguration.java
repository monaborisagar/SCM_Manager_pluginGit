package com.logilite.scm.account;

//~--- non-JDK imports --------------------------------------------------------

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

//~--- JDK imports ------------------------------------------------------------

/**
 * 
 * @author James Christian
 * 
 */
@XmlRootElement(name = "ScmAccounts")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScmAccountConfiguration implements Iterable<ScmAccount> {

	/** Field description */
	public static final String PROPERTY_ScmAccounts = "ScmAccounts";

	// ~--- constructors
	// ---------------------------------------------------------

	/**
	 * Constructs ...
	 * 
	 */
	public ScmAccountConfiguration() {
	}

	/**
	 * Constructs ...
	 * 
	 * 
	 * @param properies
	 */
	public ScmAccountConfiguration(ScmAccount ScmAccount) {
		ScmAccounts.add(ScmAccount);
	}

	/**
	 * Constructs ...
	 * 
	 * 
	 * @param ScmAccounts
	 */
	public ScmAccountConfiguration(Set<ScmAccount> ScmAccounts) {
		this.ScmAccounts.addAll(ScmAccounts);
	}

	// ~--- methods
	// --------------------------------------------------------------

	/**
	 * Method description
	 * 
	 * 
	 * @return
	 */
	@Override
	public Iterator<ScmAccount> iterator() {
		return ScmAccounts.iterator();
	}

	/**
	 * Method description
	 * 
	 * 
	 * @param otherConfiguration
	 * 
	 * @return
	 */
	public ScmAccountConfiguration merge(
			ScmAccountConfiguration otherConfiguration) {

		Set<ScmAccount> allHooks = new HashSet<ScmAccount>();

		allHooks.addAll(ScmAccounts);

		allHooks.addAll(otherConfiguration.ScmAccounts);

		return new ScmAccountConfiguration(allHooks);
	}

	/**
	 * @param newScmAccount
	 * @return
	 */
	public ScmAccountConfiguration add(ScmAccount newScmAccount) {
		this.ScmAccounts.add(newScmAccount);
		return this;
	}

	// ~--- get methods
	// ----------------------------------------------------------

	public ScmAccount getById(final String id) {
		return Iterables.find(this.ScmAccounts, new Predicate<ScmAccount>() {
			@Override
			public boolean apply(ScmAccount input) {
				return input.getId().equals(id.trim());
			}
		});
	}

	/* find by user account by its name from the Set<ScmAccount> ScmAccounts */
	public ScmAccount getByUserName(final String user) {

		return Iterables.find(this.ScmAccounts, new Predicate<ScmAccount>() {
			@Override
			public boolean apply(ScmAccount input) {
				return input.getUsername().equals(user.trim());
			}
		});
	}

	/* remove by id */
	public void remove(String id){
		ScmAccounts.remove(this.getById(id));
	}

	/**
	 * Method description
	 * 
	 * 
	 * @return
	 */
	public boolean isScmAccountAvailable() {
		return !ScmAccounts.isEmpty();
	}

	// ~--- methods
	// --------------------------------------------------------------

	// ~--- fields
	// ---------------------------------------------------------------

	/** Field description */
	@XmlElement(name = "ScmAccount")
	private final Set<ScmAccount> ScmAccounts = new HashSet<ScmAccount>();

}
