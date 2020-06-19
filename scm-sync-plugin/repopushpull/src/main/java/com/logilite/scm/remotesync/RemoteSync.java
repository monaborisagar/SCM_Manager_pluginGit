/**
 * Copyright (c) 2010, James Christian
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */

package com.logilite.scm.remotesync;

//~--- non-JDK imports --------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
//~--- JDK imports ------------------------------------------------------------
import javax.xml.bind.annotation.XmlAccessType;

/**
 * 
 * @author James Christian
 */
@XmlRootElement(name = "remotesync")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoteSync {

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public void setRemoteSite(String remoteSite) {
		this.remoteSite = remoteSite;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Constructs ...
	 * 
	 */
	RemoteSync() {
	}

	public RemoteSync(String id,String repository,String accountId,String remoteSite,
			String type) {
		super();
		this.id=id;
		this.repository=repository;
		this.remoteSite = remoteSite;
		this.accountId=accountId;
		this.type = type;
	}

	// ~--- methods
	// --------------------------------------------------------------

	/**
	 * Method description
	 * 
	 * 
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final RemoteSync other = (RemoteSync) obj;

		return Objects.equal(repository,other.repository)
				&& Objects.equal(remoteSite,other.remoteSite)
				&& Objects.equal(accountId,other.accountId)
				&& Objects.equal(type, other.type);
	}

	/**
	 * Method description
	 * 
	 * 
	 * @return
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(repository,accountId,remoteSite,type);
	}

	/**
	 * Method description
	 * 
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		// J-
		return Objects.toStringHelper(this)
				.add("id",id)
				.add("repository",repository)
				.add("remoteSite",remoteSite)
				.add("type",type)
				.add("accountId", accountId).toString();
		// J+
	}

	// ~--- set methods
	// ----------------------------------------------------------
	
	public void setId(String id){
		this.id=new String(id);
	}
	
	// ~--- get methods
	// ----------------------------------------------------------
	
	public String getId(){
		return id;
	}
	
	public String getRepository(){
		return repository;
	}
	
	public String getRemoteSite() {
		return remoteSite;
	}
	
	public String getType() {
		return type;
	}

	// ~--- fields
	// ---------------------------------------------------------------

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	private String id;
	private String repository;
	private String remoteSite;
	private String accountId;
	private String type;

}
