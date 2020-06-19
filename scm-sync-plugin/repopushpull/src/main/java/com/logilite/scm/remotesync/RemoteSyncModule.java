 
package com.logilite.scm.remotesync;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.plugin.ext.Extension;

import com.google.inject.AbstractModule;

/**
 *
 * @author James Christian
 */
@Extension
public class RemoteSyncModule extends AbstractModule
{

	 
	
  /**
   * Method description
   *
   */
  @Override
  protected void configure()
  {
    bind(RemoteSyncContext.class);
  }
}
