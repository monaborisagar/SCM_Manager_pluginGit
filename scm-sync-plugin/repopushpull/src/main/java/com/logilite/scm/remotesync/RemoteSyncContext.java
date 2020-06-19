/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package com.logilite.scm.remotesync;

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
public final class RemoteSyncContext
{

  /** Field description */
  private static final String STORE_NAME = "remotesync";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param storeFactory
   */
  @Inject
  public RemoteSyncContext(StoreFactory storeFactory)
  {
	  
    this.store = storeFactory.getStore(RemoteSyncConfiguration.class, STORE_NAME);
    
    globalConfiguration = store.get(); 
    
    if (globalConfiguration == null)
    {
      globalConfiguration = new RemoteSyncConfiguration();
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  public RemoteSyncConfiguration getConfiguration(RemoteSync remoteSync)
  {
    RemoteSyncConfiguration repoConf = new RemoteSyncConfiguration(remoteSync);
    return globalConfiguration.merge(repoConf);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public RemoteSyncConfiguration getGlobalConfiguration()
  {
    return globalConfiguration;
  }

  //~--- set methods ----------------------------------------------------------

  
  /**
   * Method description
   * To append or create new remote repository config in xml file
   *
   * @param globalConfiguration
   */
  public void setGlobalConfiguration(RemoteSyncConfiguration globalConfiguration)
  {
	  /*setting id for the each <RemoteSync> POST by the request*/
	  Iterator<RemoteSync> decodeRemoteSyncs= globalConfiguration.iterator();
	  CipherUtil cipher=CipherUtil.getInstance();
	  
	  while(decodeRemoteSyncs.hasNext()){
		  RemoteSync decodeRemoteSync=decodeRemoteSyncs.next();
		  decodeRemoteSync.setId(cipher.getKeyGenerator().createKey().substring(0,8));
	  }
	  
	  if(! this.globalConfiguration.isRemoteSyncAvailable()){
	    	/*System.out.println("new File");*/
	    	this.globalConfiguration = globalConfiguration;    	
	  }else{
		  this.globalConfiguration=this.globalConfiguration.merge(globalConfiguration);
	  }
    
    store.set(this.globalConfiguration);
  }
  
  public void updateRemoteSyncById(String remoteId,RemoteSyncConfiguration globalConfiguration){
	  RemoteSync updatedRemoteSync=globalConfiguration.iterator().next();
	  this.globalConfiguration=this.globalConfiguration.updateById(remoteId, updatedRemoteSync);
	  store.set(this.globalConfiguration);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final Store<RemoteSyncConfiguration> store;

  /** Field description */
  private RemoteSyncConfiguration globalConfiguration;
}
