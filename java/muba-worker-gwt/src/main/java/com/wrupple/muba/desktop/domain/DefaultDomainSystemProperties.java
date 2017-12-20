package com.wrupple.muba.desktop.domain;

import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class DefaultDomainSystemProperties extends DomainSystemPropertiesImpl {
	private static final long serialVersionUID = 1L;

	public DefaultDomainSystemProperties() {
		super();
		setBillingCurrency("USD");
		setCurrency("USD");
		setDefaultLocale("en");
		setEnableLiveDesktop(false);
		setName("Public domain");
		setTimestamp(new Date());
	}

}
