package com.wrupple.vegetate.server.chain.command.impl;

import java.io.PrintWriter;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.domain.VegetatePeer;
import com.wrupple.muba.catalogs.server.chain.command.PeerPresenceCommand;
import com.wrupple.muba.catalogs.server.chain.command.SusriptionService;
import com.wrupple.muba.catalogs.server.services.RequestScopedContext;
import com.wrupple.muba.catalogs.server.services.RootServiceManifest;

@Singleton
public class PeerPresenceCommandImpl implements PeerPresenceCommand {

	private final String peerCatalogId;
	private final String SUSCRIPTION_SERVICE_NAME;
	private final String VEGETATE_SERVICE_NAME;
	private final Provider<RequestScopedContext> requestScopeProvider;

	@Inject
	public PeerPresenceCommandImpl(SusriptionService suscriptionServiceManifest, RootServiceManifest rootMani,
			Provider<RequestScopedContext> requestScopeProvider, @Named("vegetate.catalog") String peerCatalogId) {
		super();
		this.peerCatalogId = peerCatalogId;
		this.SUSCRIPTION_SERVICE_NAME = suscriptionServiceManifest.getServiceName();
		VEGETATE_SERVICE_NAME = rootMani.getGrammar()[0];
		this.requestScopeProvider = requestScopeProvider;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		RequestScopedContext request = requestScopeProvider.get();
		if (request.getFormat() == null) {

			VegetatePeer peer = request.getSession().getPeerValue();

			int status;
			// set online status when catalog engine is beeing invoked, idle
			// when
			// only heart beat is beeing invoken, offline otherwise
			// use an external task to scan all clients last heartbeat and
			// update status accordingly
			if (SUSCRIPTION_SERVICE_NAME.equals(context.get(VEGETATE_SERVICE_NAME))) {
				status = VegetatePeer.STATUS_IDLE;
			} else {
				// when called explicitly (programatically) within any other
				// context service (as catalog chain does)
				status = VegetatePeer.STATUS_ONLINE;
			}

			CatalogExcecutionContext catalogContext = request.getStorageManager().spawn(request);
			String publicKey = setPeerPresence(peer, catalogContext, request, status);
			PrintWriter out = request.getScopedWriter(context);
			out.write('{');
			out.write('\"');
			out.write(VegetatePeer.PUBLIC_KEY);
			out.write('\"');
			out.write(':');
			out.write('\"');
			out.write(publicKey);
			out.write('\"');
			out.write('}');
		}

		return CONTINUE_PROCESSING;
	}

	public String setPeerPresence(VegetatePeer desktopSession, CatalogExcecutionContext context, RequestScopedContext request, int clientStatus)
			throws Exception {

		boolean changed = desktopSession.setMinimumActivityStatus(clientStatus);
		if (!context.getDomain().equals(desktopSession.getDomain())) {
			desktopSession.setDomain(context.getDomain().longValue());
			changed = true;
		}
		if (desktopSession.getStakeHolder() == null || !(desktopSession.getStakeHolder().equals(request.getSession().getStakeHolder()))) {
			desktopSession.setStakeHolder((Long) request.getSession().getStakeHolder());
			changed = true;
		}

		changed = changed || assertSocketIsUsable(desktopSession, context, request);
		if (changed) {
			context.set(context.getDomain().longValue(), peerCatalogId, CatalogActionRequest.WRITE_ACTION, desktopSession.getIdAsString(), desktopSession,
					null);
			request.getStorageManager().getWrite(false).execute(context);
		}

		return desktopSession.getPublicKey();
	}

	private boolean assertSocketIsUsable(VegetatePeer desktopSession, CatalogExcecutionContext context, RequestScopedContext request) {

		boolean isUsable = checkCachedChannelUsability(desktopSession);
		if (!isUsable) {
			request.getPeerManager().renewLease(desktopSession, request);
		}
		return !isUsable;
	}

	private boolean checkCachedChannelUsability(VegetatePeer session) {
		String token = session.getPublicKey();
		if (token == null) {
			return false;
		}
		Date expirationDate = session.getExpirationDate();
		Date now = new Date();
		if (expirationDate == null) {
			System.err.println("[LIVE DESKTOP ] client speciffied no expiration date for socket channel");
			return false;
		} else if (expirationDate.compareTo(now) <= 0) {
			System.err.println("[LIVE DESKTOP ] socket channel is expired");
			return false;
		}
		System.err.println("[LIVE DESKTOP ] socket channel is valid still");
		return true;
	}

}
