package com.logilite.scm.remotesync;

//~--- non-JDK imports --------------------------------------------------------

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aragost.javahg.commands.UpdateResult;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

//~--- JDK imports ------------------------------------------------------------

/**
 * 
 * @author James Christian
 * 
 */
@XmlRootElement(name = "RemoteSyncs")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoteSyncConfiguration implements Iterable<RemoteSync> {

	/** Field description */
	public static final String PROPERTY_RemoteSyncS = "RemoteSyncs";

	// ~--- constructors
	// ---------------------------------------------------------

	/**
	 * Constructs ...
	 * 
	 */
	public RemoteSyncConfiguration() {
	}

	/**
	 * Constructs ...
	 * 
	 * 
	 * @param properies
	 */
	public RemoteSyncConfiguration(RemoteSync remoteSync) {
		RemoteSyncs.add(remoteSync);
	}

	/**
	 * Constructs ...
	 * 
	 * 
	 * @param RemoteSyncs
	 */
	public RemoteSyncConfiguration(Set<RemoteSync> RemoteSyncs) {
		this.RemoteSyncs.addAll(RemoteSyncs);
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
	public Iterator<RemoteSync> iterator() {
		return RemoteSyncs.iterator();
	}

	/**
	 * Method description
	 * 
	 * 
	 * @param otherConfiguration
	 * 
	 * @return
	 */
	public RemoteSyncConfiguration merge(
			RemoteSyncConfiguration otherConfiguration) {

		Set<RemoteSync> allHooks = new HashSet<RemoteSync>();

		allHooks.addAll(RemoteSyncs);

		allHooks.addAll(otherConfiguration.RemoteSyncs);

		return new RemoteSyncConfiguration(allHooks);
	}

	/**
	 * @param newRemoteSync
	 * @return
	 */
	public RemoteSyncConfiguration add(RemoteSync newRemoteSync) {
		this.RemoteSyncs.add(newRemoteSync);
		return this;
	}


	// ~--- get methods
	// ----------------------------------------------------------
	
	public RemoteSync getById(final String id) {
		return Iterables.find(this.RemoteSyncs,new Predicate<RemoteSync>(){
			@Override
			public boolean apply(RemoteSync input) {
				return input.getId().equals(id);
			}
		});
	}
	
	/*find by repository by its name from the Set<RemoteSync> RemoteSyncs*/
	public RemoteSync getByName(final String reponame) {

		return Iterables.find(this.RemoteSyncs,new Predicate<RemoteSync>(){
			@Override
			public boolean apply(RemoteSync input) {
				return input.getRepository().equals(reponame.trim());
			}
		});
	}

	/*to update the remotesync from our list*/
	public RemoteSyncConfiguration updateById(String remoteId,RemoteSync updatedRemoteSync){
		
		RemoteSync oldRemoteSync=this.getById(remoteId);
		
		RemoteSync newRemoteSync=new RemoteSync(remoteId,
						updatedRemoteSync.getRepository()==null?oldRemoteSync.getRepository():updatedRemoteSync.getRepository(),
						updatedRemoteSync.getAccountId()==null?oldRemoteSync.getAccountId():updatedRemoteSync.getAccountId(),
						updatedRemoteSync.getRemoteSite()==null?oldRemoteSync.getRemoteSite():updatedRemoteSync.getRemoteSite(),
							updatedRemoteSync.getType()==null?oldRemoteSync.getType():updatedRemoteSync.getType()
				);
		this.RemoteSyncs.remove(oldRemoteSync);
		this.RemoteSyncs.add(newRemoteSync);
		return new RemoteSyncConfiguration(this.RemoteSyncs);
	}
	
	
	/**
	 * Method description
	 * 
	 * 
	 * @return
	 */
	public boolean isRemoteSyncAvailable() {
		return !RemoteSyncs.isEmpty();
	}

	// ~--- methods
	// --------------------------------------------------------------

	// ~--- fields
	// ---------------------------------------------------------------

	/** Field description */
	@XmlElement(name = "RemoteSync")
	private final Set<RemoteSync> RemoteSyncs = new HashSet<RemoteSync>();

}
