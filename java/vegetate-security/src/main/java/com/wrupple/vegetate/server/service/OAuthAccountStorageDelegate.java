package com.wrupple.vegetate.server.service;

import org.apache.shiro.subject.SimplePrincipalCollection;

public interface OAuthAccountStorageDelegate {
	public long getOrCreateStakeHolder(String realm,String memberId,long memberIdAsLong, String email, String serviceScreenName, String publicKey, String privateKey,
			SimplePrincipalCollection principals) throws Exception;
}
