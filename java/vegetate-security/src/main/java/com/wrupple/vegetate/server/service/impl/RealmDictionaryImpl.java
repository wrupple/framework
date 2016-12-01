package com.wrupple.vegetate.server.service.impl;

import javax.inject.Provider;

import org.apache.commons.chain.impl.CatalogBase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.server.service.RealmDictionary;
import com.wrupple.muba.catalogs.server.chain.command.impl.LazyCommand;
import com.wrupple.vegetate.server.service.FacebookAccountRealm;
import com.wrupple.vegetate.server.service.FacebookOAuthHandler;
import com.wrupple.vegetate.server.service.GoogleAccountRealm;
import com.wrupple.vegetate.server.service.GoogleOAuthHandler;
import com.wrupple.vegetate.server.service.TwitterAuthenticationRealm;
import com.wrupple.vegetate.server.service.TwitterOAuthHandler;

@Singleton
public class RealmDictionaryImpl extends CatalogBase implements RealmDictionary{

	@Inject
	public RealmDictionaryImpl(Provider<TwitterOAuthHandler> twitterOauthHandlerProvider,Provider<FacebookOAuthHandler> facebookHandler,Provider<GoogleOAuthHandler> googleHandler) {
		super();
		addCommand(TwitterAuthenticationRealm.class.getSimpleName(), new LazyCommand(twitterOauthHandlerProvider));
		addCommand(FacebookAccountRealm.class.getSimpleName(), new LazyCommand(facebookHandler));
		addCommand(GoogleAccountRealm.class.getSimpleName(), new LazyCommand(googleHandler));
	}
}
