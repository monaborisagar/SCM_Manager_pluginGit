

package com.logilite.scm.account;

//~--- non-JDK imports --------------------------------------------------------

import java.util.Iterator;


//~--- JDK imports ------------------------------------------------------------
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

@Path("plugins/scmaccount")
public class ScmAccountResource
{

  /**
   * Constructs ...
   *
   *
   * @param context
   */
  @Inject
  public ScmAccountResource(ScmAccountContext context)
  {
    Subject subject = SecurityUtils.getSubject();

    subject.checkRole(Role.ADMIN);
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
  public ScmAccountConfiguration getConfiguration()
  {
	  Iterator<ScmAccount> originalIterator=context.getGlobalConfiguration().iterator();
	  ScmAccountConfiguration displayConfig=new ScmAccountConfiguration();
	  
	  while(originalIterator.hasNext()){
		  ScmAccount originalScmAccount=originalIterator.next();
		  ScmAccount duplScmAccount=new ScmAccount(
				  originalScmAccount.getId(),
				  originalScmAccount.getAccountName(),
				  originalScmAccount.getUsername(), 
				  "********" );
				  
		  displayConfig.add(duplScmAccount);
	  }
	  
	  return displayConfig;
  }

  @GET
  @Path( "{name}" )
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public ScmAccount getByName( @PathParam( "name" ) String reponame ) {
	  return this.context.getGlobalConfiguration().getByUserName(reponame);
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
  public void setConfiguration(ScmAccountConfiguration configuration)
  {
	  
	  context.setGlobalConfiguration(configuration);
  }

  @DELETE
  @Path("{id}")
  public void deleteScmAccount(@PathParam("id") String id) {
      context.deleteScmAccount(id);
  }
  
  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final ScmAccountContext context;
}
