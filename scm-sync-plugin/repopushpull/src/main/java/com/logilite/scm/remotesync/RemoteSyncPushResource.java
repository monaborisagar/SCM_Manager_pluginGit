package com.logilite.scm.remotesync;

//~--- non-JDK imports --------------------------------------------------------

//~--- JDK imports ------------------------------------------------------------
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.SCMContextProvider;
import sonia.scm.repository.HgRepositoryHandler;
import sonia.scm.security.CipherUtil;
import sonia.scm.security.Role;

import com.aragost.javahg.BaseRepository;
import com.aragost.javahg.Changeset;
import com.aragost.javahg.commands.PushCommand;
import com.google.inject.Inject;
import com.logilite.scm.account.ScmAccount;
import com.logilite.scm.account.ScmAccountContext;

/**
 * 
 * @author James Christian
 */

@Path("plugins/remotesyncpush")
public class RemoteSyncPushResource {

	private static final Logger logger =
		    LoggerFactory.getLogger(RemoteSyncPushResource.class);
	/**
	 * Constructs ...
	 * 
	 * 
	 * @param context
	 */
	@Inject
	public RemoteSyncPushResource(SCMContextProvider scmContext,ScmAccountContext scmAccountContext,
			RemoteSyncContext remoteSyncContext, HgRepositoryHandler handler) {
		Subject subject = SecurityUtils.getSubject();

		//subject.checkRole(Role.ADMIN);

		this.handler = handler;
		this.scmContext = scmContext;
		this.remoteSyncContext = remoteSyncContext;
		this.scmAccountContext=scmAccountContext;
	}

	// ~--- get methods
	// ----------------------------------------------------------

	// ~--- set methods
	// ----------------------------------------------------------

	/**
	 * Method description
	 * 
	 * 
	 * @param configuration
	 */
	@POST
	@Path("{reponame}/{force}/{branch}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response makePush(@PathParam("reponame") String pushRepositoryName,@PathParam("force") Boolean forcePush,@PathParam("branch") String pushRepositoryBranch) {
		
		/*getting RemoteSync object from globalConfiguration, which we stored in xml file*/
		RemoteSync pushRemote=remoteSyncContext.getGlobalConfiguration().getByName(pushRepositoryName);
		ScmAccount accountCredentials=scmAccountContext.getGlobalConfiguration().getById(pushRemote.getAccountId());
		
		File hgRepoDir = new File(handler.getConfig().getRepositoryDirectory(),
				pushRemote.getRepository());


		String pushUrl=
				new StringBuilder(pushRemote.getRemoteSite()).insert(pushRemote.getRemoteSite().indexOf("://")+3,
						accountCredentials.getUsername() + ":" +CipherUtil.getInstance().decode(accountCredentials.getPassword()) + "@" ).toString();
		BaseRepository hgRepository=BaseRepository.open(hgRepoDir);
		
		
		PushCommand hgPush=new PushCommand(hgRepository);
		hgPush.branch(pushRepositoryBranch);
		
		if(forcePush)
			hgPush.force();

		StringBuilder responses = new StringBuilder("'");
		try {
			List<Changeset> changeSet = hgPush.execute(pushUrl);
			if (changeSet.size()==0){
				responses.append("No Changeset to push");
			}else{
				responses.append(changeSet.size() + " changes pushed");
			}
		} catch (Exception e) {
			logger.error("Exception while pushing " ,e);
			responses.append("Exception while pushing, ").append(e.getLocalizedMessage()).append("'");
			return Response.ok(
				      new GenericEntity<String>(responses.toString()) {}
				    ).status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		responses.append("'");
		return Response.ok(new GenericEntity<String>(responses.toString()){}).build();
	}

	// ~--- fields
	// ---------------------------------------------------------------

	/** Field description */
	private final RemoteSyncContext remoteSyncContext;
	private final SCMContextProvider scmContext;
	private final ScmAccountContext scmAccountContext;
	private final HgRepositoryHandler handler;
}
