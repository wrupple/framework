package com.wrupple.base.server.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Provider;
import javax.servlet.ServletContext;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.base.server.domain.SignedAuthenticationToken;
import com.wrupple.home.server.domain.WruppleAccount;
import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.server.service.BusinessEventSuscriptionManager;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.Person;
import com.wrupple.vegetate.server.chain.CatalogManager;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

public class SignatureRealm extends AuthorizingRealm {

	private final Provider<CatalogManager> daoFactory;
	private final Provider<CatalogExcecutionContext> contextProvider;
	private final Provider<ServletContext> servletContextP;
	private final Provider<BusinessEventSuscriptionManager> besmP;
	private final RandomNumberGenerator saltGenerator;

	@Inject
	public SignatureRealm(RandomNumberGenerator saltGenerator, Provider<BusinessEventSuscriptionManager> besmP, Provider<ServletContext> servletContextP,
			CacheManager cacheManager, Provider<CatalogManager> daoFactory, Provider<CatalogExcecutionContext> contextProvider) {
		super(cacheManager, null);
		this.saltGenerator = saltGenerator;
		this.daoFactory = daoFactory;
		this.besmP = besmP;
		this.servletContextP = servletContextP;
		this.contextProvider = contextProvider;
		setAuthenticationTokenClass(SignedAuthenticationToken.class);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		WruppleAccount account = principals.oneByType(WruppleAccount.class);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(account.getRoles());
		info.setStringPermissions(account.getPermissions());
		return info;

	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken t) throws AuthenticationException {
		SignedAuthenticationToken token = (SignedAuthenticationToken) t;

		SignatureGenerator signer;
		String principal = (String) token.getPrincipal();
		try {
			// SE IF THERES A BOUD SIGNATURE GENERATOR
			signer = ((Injector) servletContextP.get().getAttribute(Injector.class.getName()))
					.getInstance(Key.get(SignatureGenerator.class, Names.named(principal)));
		} catch (ConfigurationException e) {
			signer = findPeer(principal);
		}

		if (signer == null) {

			System.err.println("key unrecognized");
		} else {
			if ((token.getId()==null||signer.isInTimestampThreshold(token.getTimestamp()) )&& signer.doSignatureMatch(token.getSignature(), token.getRawMessage(),token.getId())) {

				CatalogExcecutionContext context = contextProvider.get();
				context.getDomainContext().switchDomain(CatalogEntry.WRUPPLE_ID);
				List<WruppleAccount> data;
				try {
					CatalogDataAccessObject<WruppleAccount> dsm = daoFactory.get().getOrAssembleDataSource(WruppleAccount.CATALOG, context,
							WruppleAccount.class);
					data = (List<WruppleAccount>) dsm.read(creteLogiFilterData(signer.getStakeHolder()));
				} catch (Exception e) {
					System.err.println("[data exception] " + e);
					System.err.println(Arrays.toString(e.getStackTrace()));
					throw new AuthenticationException(e);
				}
				if (data == null || data.isEmpty()) {
					System.err.println("no matching account");
					throw new AuthenticationException();
				} else {
					WruppleAccount account = data.get(0);

					SimplePrincipalCollection principals = new SimplePrincipalCollection(account.getName(), getName());
					principals.add(account.getPersonId(), getName());
					principals.add(account, getName());
					principals.add(signer, getName());
					SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principals, t.getCredentials());

					return info;
				}

			} else {
				System.err.println("signatures aint matching");
			}
		}
		throw new AuthenticationException();
	}

	private SignatureGenerator findPeer(String principal) {
		BPMPeer peer = besmP.get().getPeerByKey(principal);
		if (peer == null) {
			return null;
		} else {
			return new SignatureGeneratorImpl(saltGenerator, peer.getPublicKey(), peer.getPrivateKey(),(Long)peer.getStakeHolder());
		}

	}

	private FilterData creteLogiFilterData(long personId) {
		FilterData filter = FilterDataUtils.createSingleFieldFilter(Person.PERSON_ID_FIELD, personId);
		return filter;
	}

}
