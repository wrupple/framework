package com.wrupple.muba.desktop.server;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.wrupple.muba.bpm.server.service.BPMWebEventRequestTokenizer;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestProcessor;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.RequestScopedContext;
import com.wrupple.vegetate.server.services.SessionContext;

/**
 * a way for users to create somewhat customized urls of the form
 * 
 * [url base]/[user domain]/[ user defined name of event]
 * 
 * the request tokenizer picks up the name of the event and sees if there are
 * any Triggers in the user's domain to handle suck event
 * 
 * the request is processesed as a catalog request for each trigger found
 * 
 * 
 * 
 * @author japi
 *
 */
@Singleton
public class BPMWebEventListener extends WruppleCatalogServlet {

	@Inject
	public BPMWebEventListener(Provider<SessionContext> usipp, Provider<CatalogExcecutionContext> contextProvider, Provider<CatalogRequestProcessor> catalog,
			ObjectMapper mapper, CatalogServiceManifest manifest, Provider<BPMWebEventRequestTokenizer> tp, Provider<RequestScopedContext> rscc) {
		super(usipp, contextProvider, catalog, mapper, manifest, tp, rscc);
	}

	private static final long serialVersionUID = -4302633292132668531L;

}
