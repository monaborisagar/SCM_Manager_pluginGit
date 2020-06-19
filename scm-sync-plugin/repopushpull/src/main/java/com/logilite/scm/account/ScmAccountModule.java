package com.logilite.scm.account;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.plugin.ext.Extension;

import com.google.inject.AbstractModule;

/**
 * 
 * @author James Christian
 */
@Extension
public class ScmAccountModule extends AbstractModule {

	/**
	 * Method description
	 * 
	 */
	@Override
	protected void configure() {
		bind(ScmAccountContext.class);
	}
}
