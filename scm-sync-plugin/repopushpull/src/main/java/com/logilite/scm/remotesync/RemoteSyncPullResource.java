package com.logilite.scm.remotesync;

//~--- non-JDK imports --------------------------------------------------------

//~--- JDK imports ------------------------------------------------------------
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
import com.aragost.javahg.commands.PullCommand;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.inject.Inject;
import com.logilite.scm.account.ScmAccount;
import com.logilite.scm.account.ScmAccountContext;

/**
 * @author Deeepak Pansheriya
 * @author James Christian
 * 
 */

@Path("plugins/remotesyncpull")
public class RemoteSyncPullResource {

	private static final Logger logger =
		    LoggerFactory.getLogger(RemoteSyncPullResource.class);
	/**
	 * Constructs ...
	 * 
	 * 
	 * @param context
	 */
	@Inject
	public RemoteSyncPullResource(SCMContextProvider scmContext,ScmAccountContext scmAccountContext,
			RemoteSyncContext remoteSyncContext, HgRepositoryHandler handler) {
		Subject subject = SecurityUtils.getSubject();

		//subject.checkRole(Role.ADMIN);
		this.handler =  handler;
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
	@Path("{reponame}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response makPull(@PathParam("reponame") String pullRepository) {

		RemoteSync pullRemote = remoteSyncContext.getGlobalConfiguration()
				.getByName(pullRepository);

		ScmAccount accountCredentials=scmAccountContext.getGlobalConfiguration().getById(pullRemote.getAccountId());
		
		
		File hgRepoDir = new File(handler.getConfig().getRepositoryDirectory(),
				 pullRemote.getRepository());

		String pullSource = new StringBuilder(pullRemote.getRemoteSite())
				.insert(pullRemote.getRemoteSite().indexOf("://") + 3,
						accountCredentials.getUsername()
								+ ":"
								+ CipherUtil.getInstance().decode(
										accountCredentials.getPassword()) + "@")
				.toString();

		File hgrcFile = new File(hgRepoDir, ".hg/hgrc");

		try {
			List<String> hookLines = Files.readLines(hgrcFile,
					Charset.defaultCharset(),
					new LineProcessor<List<String>>() {

						List<String> foundHooks = new ArrayList<String>();
						boolean found = false;

						@Override
						public boolean processLine(String line)
								throws IOException {
							/*System.out.println(" found : " + found);*/
							if (line.contains("[hooks]")) {
								found = true;
							}
							if (!line.contains("[hooks]") && found
									&& line.matches("\\[(.*?)\\]")) {
								found = false;
							}
							if (!found) {
								foundHooks.add(line);
							}
							return true;
						}

						@Override
						public List<String> getResult() {
							return foundHooks;
						}
					});
			/* after removing hooklines in List writing entire hgrc file again */
			if (hookLines.size() != 0) {
				String hgrcContents = "";
				for (String line : hookLines) {
					hgrcContents = hgrcContents + line + "\n";
				}
				Files.write(hgrcContents, hgrcFile, Charset.defaultCharset());
			}

		} catch (IOException ex) {
			logger.error("Error while reading hooklines ",ex);
			 return Response.ok(
				      new GenericEntity<String>(ex.getMessage()) {}
				    ).build();
		}

		/* after hgrc changes, trying to do pull */
		BaseRepository hgRepository = BaseRepository.open(hgRepoDir);
		PullCommand pullCmnd = new PullCommand(hgRepository);
		StringBuilder responses = new StringBuilder("'");
		try {
			List<Changeset> pullChangeset = pullCmnd.execute(pullSource);
			
			if (pullChangeset.size() > 0) {
				/*UpdateCommand hgUpdate = new UpdateCommand(hgRepository);
				UpdateResult updateResult = hgUpdate.execute();
				System.out.println("Total Updated << "
						+ updateResult.getUpdated() + " >> || Merged << "
						+ updateResult.getMerged() + " >> || Removed << "
						+ updateResult.getRemoved() + " >> || Unresolved << "
						+ updateResult.getUnresolved() + " >>");*/
				responses.append("Pulled ").append( pullChangeset.size() ).append(" Change sets");
			}else{
				responses.append("No Changes Found");
			}

			/*Iterator<Changeset> changes = pullChangeset.iterator();
			while (changes.hasNext()) {
				Changeset change = changes.next();
				responses.append("Pull Changes : Branch <<"
						+ change.getBranch() + ">> Message << "
						+ change.getMessage() + " >></BR>\n");
			}*/
		} catch (Exception e) {
			logger.error("Error while Pulling ",e);
			 return Response.ok(
				      new GenericEntity<String>("'Can not pull " + e.getMessage() + "'") {}
				    ).status(Response.Status.INTERNAL_SERVER_ERROR).build();
			 
		}

		responses.append("'");
		 return Response.ok(
			      new GenericEntity<String>(responses.toString()) {}
			    ).build();
	}

	// ~--- fields
	// ---------------------------------------------------------------

	/** Field description */
	private final RemoteSyncContext remoteSyncContext;
	private final SCMContextProvider scmContext;
	private final ScmAccountContext scmAccountContext;
	private final HgRepositoryHandler handler;
}
