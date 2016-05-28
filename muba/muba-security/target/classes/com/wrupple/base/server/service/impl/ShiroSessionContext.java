package com.wrupple.base.server.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wrupple.base.server.domain.SignedAuthenticationToken;
import com.wrupple.base.server.domain.SystemAuthenticationToken;
import com.wrupple.base.server.service.SystemRealm;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.Person;
import com.wrupple.vegetate.domain.VegetatePeer;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.chain.command.impl.VegetateServiceImpl;
import com.wrupple.vegetate.server.services.ContextRewriter;
import com.wrupple.vegetate.server.services.SessionContext;

/**
 * 
 * Principals are assumed to be in order
 * 
 * 1.- String unique screenName (e-mail addess, twitter token, etc..) 2-- Long
 * person Id 3.- Implementation specific Obejct
 * 
 * 
 * 
 * @author japi
 *
 */
public class ShiroSessionContext extends ContextBase implements SessionContext {
	private static final Logger log = LoggerFactory.getLogger(ShiroSessionContext.class);
	private static final long serialVersionUID = 8347036393166555684L;

	private final String catalogMasterRole;
	private final ContextRewriter rewriter;

	@Inject
	public ShiroSessionContext( @Named("domainMasterRole") String catalogMasterRole, ContextRewriter rewriter

	) {
		this.catalogMasterRole = catalogMasterRole;
		this.rewriter = rewriter;
	}

	@Override
	public void setStakeHolder(long stakeHolder) {

		log.warn("[CHANGE SESSION STAKEHOLDER] {}",stakeHolder);
		Subject currentUser = SecurityUtils.getSubject();

		PrincipalCollection principals = new SimplePrincipalCollection(new SystemAuthenticationToken(stakeHolder, currentUser.getPrincipal()),
				SystemRealm.NAME);
		currentUser.runAs(principals);
	}

	@Override
	public void releaseAuthority() {
		PrincipalCollection released = SecurityUtils.getSubject().releaseRunAs();
		System.err.println("[AUTHORITY RELEASED: ]" + released);
	}

	@Override
	public Long getStakeHolder() {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated() || currentUser.isRemembered()) {
			PrincipalCollection principalCollection = currentUser.getPrincipals();

			Long userId = principalCollection.oneByType(Long.class);
			if (userId == null) {

				return CatalogEntry.PUBLIC_ID;
			} else {
				return userId;
			}
		} else {
			return CatalogEntry.PUBLIC_ID;
		}
	}

	@Override
	public boolean isMaster() {
		return isGranted(catalogMasterRole);
	}

	@Override
	public boolean isGranted(String roleCatalogMaster) {
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser.hasRole(roleCatalogMaster);
	}

	@Override
	public boolean hasPermissionsToProcessContext(Context context, VegetateServiceManifest manifest) {

		String serviceId = manifest.getServiceName();
		rewriter.rewriteContext(context, manifest, serviceId, this);
		if (serviceId == null) {
			throw new NullPointerException("Unable to determine context's processing service");
		}
		String[] tokenMappings = manifest.getUrlPathParameters();

		if (tokenMappings == null || tokenMappings.length == 0) {
			// check for permission to use service

			return SecurityUtils.getSubject().isPermitted(serviceId);
		} else {

			int bufferSize = serviceId.length();
			String[] tokenValues = new String[tokenMappings.length];
			String tokenValue;
			Object rawValue;
			for (int i = 0; i < tokenMappings.length; i++) {
				rawValue = context.get(tokenMappings[i]);
				if (rawValue != null) {
					tokenValue = String.valueOf(rawValue);
					bufferSize = bufferSize + tokenValue.length();
					tokenValues[i] = tokenValue;
				}
			}
			bufferSize = bufferSize + tokenMappings.length;

			StringBuffer buffer = new StringBuffer(bufferSize);
			buffer.append(serviceId);
			for (int i = 0; i < tokenValues.length; i++) {
				tokenValue = tokenValues[i];
				if (tokenValue == null) {
					break;
				}
				buffer.append(':');
				buffer.append(tokenValue);
			}
			String permission = buffer.toString();
			Subject subject = (Subject) SecurityUtils.getSubject();

			return subject.isPermitted(permission);
		}

	}

	@Override
	public boolean hasPermission(String string) {
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser.isPermitted(string);
	}

	public long geNontenanttUserDomain() {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated() || currentUser.isRemembered()) {
			return CatalogEntry.WRUPPLE_ID;
		} else {
			return CatalogEntry.PUBLIC_ID;
		}
	}




	@Override
	public void processAccessToken(String publicKey, String accessToken, String message,String id,Date timestamp) throws SecurityException {
		
		Subject subject = SecurityUtils.getSubject();
		SignedAuthenticationToken token = new SignedAuthenticationToken(publicKey, message, accessToken,id,timestamp);
		log.debug("process authentication token {}" , token);
		subject.login(token);
	}


	/*
	 * ////////////////////////////////////////
	 * 
	 * CONTEXT METHODS
	 */// ////////////////////////////////////

	@Override
	public Object get(Object arg0) {
		if (arg0 == null) {
		} else {

			/*
			 * String key = (String) arg0; if (key.equals("twitterScreenName"))
			 * { // TODO throw an exception that the client undestands as a
			 * reason to ask // for twitter credentials
			 * 
			 * }
			 */
		}

		return null;
	}

	@Override
	public Object put(Object arg0, Object arg1) {
		return null;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean containsKey(Object arg0) {
		return false;
	}

	@Override
	public boolean containsValue(Object arg0) {
		return false;
	}

	@Override
	public Set entrySet() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set keySet() {
		return null;
	}

	@Override
	public void putAll(Map arg0) {

	}

	@Override
	public Object remove(Object arg0) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Collection values() {
		return null;
	}


	@Override
	public <T> T getStakeHolderPrincipal(Class<T> clazz) {
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser.getPrincipals().oneByType(clazz);
	}



	
}
