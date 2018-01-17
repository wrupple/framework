package com.wrupple.muba.desktop.shared.services.impl;

import com.wrupple.muba.bpm.domain.SessionContextDTO;
import com.wrupple.muba.worker.domain.BPMPeer;
import com.wrupple.vegetate.shared.services.BPMPeerManager;

/**
 * 
 * 
 * FIXME mirrors GAEBusinessEventSuscriptionManager
 * 
 * 
 * @author japi
 *
 */
public class BPMPeerManagerImpl implements BPMPeerManager {

	private SessionContextDTO principal;
	private String principalHost;

	@Override
	public String getDomain() {
		return principal.getPeer().getCatalogDomain();
	}

	/**
	 * 
	 * in the www domain wrupple.com, is me in the twitter.com domain eljapi is
	 * me in the 183030 domain 229394 is me
	 * 
	 * @return My Uri in the namespace
	 */
	public String getPeer() {
		return principal.getPeer().getIdAsString();
	}

	@Override
	public String getPublicKey() {
		return principal.getPeer().getPublicKey();
	}

	@Override
	public String getPrivateKey() {
		return principal.getPeer().getPrivateKey();
	}

	@Override
	public BPMPeer getPeer(String hostId) {
		if (hostId == null || hostId.equals(principalHost)) {
			return this.principal.getPeer();
		} else {
			// FIXME When a catalog is received via CatalogDescription Service
			// that
			// contains a reference to an unknown host, do all requests necesary
			// to get PeerData on that Host
            // TODO Build a catalog peer to comunicate setRuntimeContext the given peer both
            // client or server side
			// TODO if the host is not the local host, then some mechanism must
            // be fired previously to athenticate setRuntimeContext that host
            throw new IllegalArgumentException("Desktop client does not currently support communication setRuntimeContext foreign hosts");
        }

	}

	@Override
	public void setPrincipal(Object localPrincipal) {
		this.principal = (SessionContextDTO) localPrincipal;
		// TODO verify principalHost matches window's host?
		this.principalHost = principal.getPeer().getHost();
		principal.getPeer().setHost(null);
	}

	@Override
	public String getHost() {
		return principalHost;
	}

}
