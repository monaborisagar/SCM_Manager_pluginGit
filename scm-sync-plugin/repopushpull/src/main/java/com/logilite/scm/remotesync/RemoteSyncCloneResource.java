package com.logilite.scm.remotesync;

//~--- non-JDK imports --------------------------------------------------------

//~--- JDK imports ------------------------------------------------------------
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import sonia.scm.SCMContextProvider;
import sonia.scm.security.CipherUtil;
import sonia.scm.security.Role;

import com.aragost.javahg.BaseRepository;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.logilite.scm.account.ScmAccount;
import com.logilite.scm.account.ScmAccountContext;

/**
 * 
 * @author James Christian
 */

@Path("plugins/remotesyncclone")
public class RemoteSyncCloneResource {

	/**
	 * Constructs ...
	 * 
	 * 
	 * @param remoteContext
	 */
	@Inject
	public RemoteSyncCloneResource(SCMContextProvider scmContext,ScmAccountContext scmAccountContext,
			RemoteSyncContext remoteContext) {
		Subject subject = SecurityUtils.getSubject();

		//subject.checkRole(Role.ADMIN);
		this.remoteContext = remoteContext;
		this.scmContext = scmContext;
		this.scmAccountContext=scmAccountContext;
	}

	// ~--- set methods
	// ----------------------------------------------------------

	/**
	 * Method description
	 * 
	 * To create repositories setting on cloning the repository
	 * 
	 * @param configuration
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void setConfiguration(RemoteSyncConfiguration configuration) {
		
		System.out.println("remote clone called for post");
		
		/* cloning the repository first */
		 Iterator<RemoteSync> newCloneRepositories=configuration.iterator();
		  while(newCloneRepositories.hasNext()){
			  
			  RemoteSync newCloneRepo=newCloneRepositories.next();
			  /*getting credentials from ScmAccount accountId*/
			  ScmAccount accountCrendentials=scmAccountContext.getGlobalConfiguration().getById(newCloneRepo.getAccountId());

			  String fullCloneUrl=new StringBuilder(newCloneRepo.getRemoteSite()).insert(newCloneRepo.getRemoteSite().indexOf("://")+3,
					  accountCrendentials.getUsername() + ":" + CipherUtil.getInstance().decode(accountCrendentials.getPassword()) + "@" ).toString();
			  System.out.println("Trying to clone on repository : " + newCloneRepo.getRepository());
			  
			  File cloneHgRepoDir=new File(scmContext.getBaseDirectory().getAbsoluteFile(),"repositories/hg/" + newCloneRepo.getRepository());
			  
			  /*cloning is called*/
			  BaseRepository hgCloneRepository=BaseRepository.clone(cloneHgRepoDir,fullCloneUrl);
			  /* after cloning done, writing hgrc file for default-push setting*/
			  File cloneHgrcFile=new File(cloneHgRepoDir.getAbsoluteFile(),".hg/hgrc");
			  try {
				Files.append("[paths]\ndefault-push = " + newCloneRepo.getRemoteSite() +"\n",cloneHgrcFile,Charset.defaultCharset());
			  } catch (IOException e) {
				  System.out.println("Failed to write default-push on clone on repository : " + newCloneRepo.getRepository());
				e.printStackTrace();
			}
		  }
		/*
		 * after cloning repo, saving username and password and repo, for push
		 * and pull purpose
		 */
		remoteContext.setGlobalConfiguration(configuration);
	}

	// ~--- fields
	// ---------------------------------------------------------------

	/** Field description */

	private final SCMContextProvider scmContext;
	private final RemoteSyncContext remoteContext;
	private final ScmAccountContext scmAccountContext;
}
