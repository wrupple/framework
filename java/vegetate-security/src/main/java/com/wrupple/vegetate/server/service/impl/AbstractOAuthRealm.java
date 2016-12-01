package com.wrupple.vegetate.server.service.impl;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.wrupple.vegetate.server.service.OAuthAccountStorageDelegate;

public abstract class AbstractOAuthRealm extends AuthenticatingRealm {

	private final OAuthAccountStorageDelegate delegate;
	public AbstractOAuthRealm(CacheManager cache, OAuthAccountStorageDelegate delegate,Class<?> name, Class<? extends AuthenticationToken> tokenClass) {
		super(cache);
		this.delegate=delegate;
		setName(name.getSimpleName());
		setAuthenticationTokenClass(tokenClass);
	}

	
	protected SimplePrincipalCollection buildPrincipals(String email,String thirdPartyId,long thirdPartyNumericId, String thirdPartyScreenName, String thirdPartyPublicKey, String thirdPartyPrivateKey){
		SimplePrincipalCollection principals = new SimplePrincipalCollection(email, getName());
		long personId = -1;
		try {
			personId = delegate.getOrCreateStakeHolder(getName(),thirdPartyId,thirdPartyNumericId, email, thirdPartyScreenName, thirdPartyPublicKey, thirdPartyPrivateKey, principals);
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
		principals.add(personId, getName());
		return principals;
	}
}
