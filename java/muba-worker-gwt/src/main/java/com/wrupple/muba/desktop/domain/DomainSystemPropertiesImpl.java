package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.bpm.domain.DomainSystemProperties;
import com.wrupple.vegetate.domain.CatalogEntry;

import java.util.Date;
import java.util.List;

public class DomainSystemPropertiesImpl implements DomainSystemProperties, CatalogEntry {

	private static final long serialVersionUID = 4457031720022060341L;
	private String name;
	private String homeImage;
	private String activityPresenterToolbarHeight;
	private Date timeStamp;
	private String defaultLocale;
	private Boolean localizedCatalogs;
	private Boolean enableLiveDesktop;
	private String billingCurrency;
	private String currency;

	public Boolean getEnableLiveDesktop() {
		if (enableLiveDesktop == null) {
			return false;
		}
		return enableLiveDesktop;
	}

	public void setEnableLiveDesktop(Boolean enableLiveDesktop) {
		this.enableLiveDesktop = enableLiveDesktop;
	}

	public String getBillingCurrency() {
		return this.billingCurrency;
	}

	public void setBillingCurrency(String c) {
		this.billingCurrency = c;
	}

	public boolean getLocalizedCatalogs() {
		if (localizedCatalogs == null) {
			return false;
		}
		return localizedCatalogs;
	}

	public void setLocalizedCatalogs(boolean localizedCatalogs) {
		this.localizedCatalogs = localizedCatalogs;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public DomainSystemPropertiesImpl() {
		setTimestamp(new Date());
	}

	@Override
	public String getIdAsString() {
		return getName();
	}

	@Override
	public void setIdAsString(String id) {
		setName(id);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getDomain() {
		return CatalogEntry.WRUPPLE_ID;
	}

	private Boolean anonymouslyVisible;
	private List<String> globalContextExpressions;

	public void setGlobalContextExpressions(List<String> globalContextExpressions) {
		this.globalContextExpressions = globalContextExpressions;
	}

	@Override
	public boolean isAnonymouslyVisible() {
		if (anonymouslyVisible == null) {
			return false;
		}
		return anonymouslyVisible;
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {
		this.anonymouslyVisible = p;
	}

	public Date getTimestamp() {
		return timeStamp;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimestamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String getHomeImage() {
		return homeImage;
	}

	public void setHomeImage(String homeImage) {
		this.homeImage = homeImage;
	}

	public String getActivityPresenterToolbarHeight() {
		return activityPresenterToolbarHeight;
	}

	public void setActivityPresenterToolbarHeight(String activityPresenterToolbarHeight) {
		this.activityPresenterToolbarHeight = activityPresenterToolbarHeight;
	}

	@Override
	public String getCatalog() {
		return CATALOG;
	}

	@Override
	public Long getId() {
		return CatalogEntry.WRUPPLE_ID;
	}

	@Override
	public String getImage() {
		return null;
	}

	@Override
	public List<String> getGlobalContextExpressions() {
		return globalContextExpressions;
	}

	@Override
	public void setDomain(Long domain) {

	}

}
