

package com.logilite.scm.remotesync;

//~--- non-JDK imports --------------------------------------------------------

import java.util.Iterator;

//~--- JDK imports ------------------------------------------------------------
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import sonia.scm.security.Role;

import com.google.inject.Inject;

/**
 *
 * @author James Christian
 */

@Path("plugins/remotesync")
public class RemoteSyncResource
{

  /**
   * Constructs ...
   *
   *
   * @param context
   */
  @Inject
  public RemoteSyncResource(RemoteSyncContext context)
  {
    Subject subject = SecurityUtils.getSubject();

    //subject.checkRole(Role.ADMIN);
    this.context = context;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public RemoteSyncConfiguration getConfiguration()
  {
	  Iterator<RemoteSync> originalIterator=context.getGlobalConfiguration().iterator();
	  RemoteSyncConfiguration displayConfig=new RemoteSyncConfiguration();
	  
	  while(originalIterator.hasNext()){
		  RemoteSync originalRemoteSync=originalIterator.next();
		  RemoteSync duplRemoteSync=new RemoteSync(originalRemoteSync.getId(),
				  originalRemoteSync.getRepository(), 
				  originalRemoteSync.getAccountId(),
				  originalRemoteSync.getRemoteSite(), 
				  originalRemoteSync.getType());
				  
		  displayConfig.add(duplRemoteSync);
	  }
	  
	  return displayConfig;
  }
/*
  @GET
  @Path( "{id}" )
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public RemoteSync getById( @PathParam( "id" ) String id ) {
	  return this.context.getGlobalConfiguration().getById(id);
  }
*/  
  @GET
  @Path( "{name}" )
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public RemoteSync getByName( @PathParam( "name" ) String reponame ) {
	  return this.context.getGlobalConfiguration().getByName(reponame);
  }
  
  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param configuration
   */
  @POST
  @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public void setConfiguration(RemoteSyncConfiguration configuration)
  {
	  
	  context.setGlobalConfiguration(configuration);
  }
  
  
  
  /**
   * Method description
   *
   *
   * @param configuration
   */
  @PUT
  @Path("{id}")
  @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public void updateByRemoteId( @PathParam( "id" ) String remoteId,RemoteSyncConfiguration configuration) {
	  context.updateRemoteSyncById(remoteId,configuration);
	  
  }
  
  
  

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final RemoteSyncContext context;
}
