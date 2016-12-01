package com.wrupple.vegetate.server.chain.command.impl;

import javax.inject.Inject;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import com.wrupple.muba.catalogs.domain.VegetateAuthenticationToken;

public class OAuthRequestProcessor extends LookupCommand{


	@Inject
	public OAuthRequestProcessor(CatalogFactory factory ) {
		super(factory);
		super.setCatalogName(VegetateAuthenticationToken.REALM_PARAMETER);
		super.setNameKey(VegetateAuthenticationToken.REALM_PARAMETER);
	}
}
