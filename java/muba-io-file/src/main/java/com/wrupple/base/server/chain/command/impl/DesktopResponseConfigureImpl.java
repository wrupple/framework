package com.wrupple.base.server.chain.command.impl;

import javax.inject.Inject;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.desktop.domain.DomainSystemPropertiesImpl;
import com.wrupple.muba.desktop.server.chain.command.DesktopResponseConfigure;
import com.wrupple.muba.desktop.server.domain.DesktopBuilderContext;

public class DesktopResponseConfigureImpl implements DesktopResponseConfigure {

	/*private final SocketChannelSuscriptionManager socketManager;
	private final @Named("desktop.cookie")String rememberMeTokenVariable;
	private final @Named("domainPropsSessionAttr") String domainPropsSessionAttr;
	private final HttpSession session;
	private final DataStoreManager daoFactory;*/
	DomainSystemPropertiesImpl properties;

	@Inject
	public DesktopResponseConfigureImpl(DomainSystemPropertiesImpl properties) {
		
		this.properties=properties;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		DesktopBuilderContext context = (DesktopBuilderContext) c;
		String rememberMeToken = null;//WriteAuditTrailsImpl.getCookieValue(context.getRequest(), rememberMeTokenVariable);

		/*
		 * TODO parameter processing, could be a fail-safe chain of its own
		 */

		// push update channel
		if (properties.getEnableLiveDesktop()) {
			//DomainRegistryDAO preferencesDao;
			long domainId = context.getDomain();
			long personid = context.getRuntimeContext().getSession().getUserUniqueId();
			/*if (preferencesDao.getEnableLiveDesktop(domainId, personid)) {
				String channelToken;
				BPMClientData desktopSession;
				CatalogDataAccessObject<BPMClientData> dsm = daoFactory.getOrAssembleDataSource(BPMClientData.CATALOG, context, BPMClientData.class);
				if (rememberMeToken == null) {
					// new client or a long unseen one
					desktopSession = socketManager.createDesktopSession(domainId, personid);
					channelToken = socketManager.getUsableSocketChannel(desktopSession, dsm, context);
					rememberMeToken = socketManager.getCookieToken(desktopSession);
					
					// a desktop session with a usable socket
				} else {
					// returning user
					desktopSession = socketManager.getSessionFromCookieToken(rememberMeToken);
					if(desktopSession==null){
						//nope, don't know this one, new client for as much as i know
						desktopSession = socketManager.createDesktopSession(domainId, personid);
						channelToken = socketManager.getUsableSocketChannel(desktopSession, dsm, context);
						rememberMeToken = socketManager.getCookieToken(desktopSession);
					}else{
						channelToken = socketManager.verifySessionSuscriptions(desktopSession, context, dsm);
					}
				}
				WriteAuditTrailsImpl.addCookie(context.getResponse(), rememberMeTokenVariable, rememberMeToken, 60*60*24*365);
				parameter.addParameter(CatalogSocketListener.CHANNEL_TOKEN_PARAMETER, channelToken);
			}*/

		}

		return CONTINUE_PROCESSING;
	}

}
