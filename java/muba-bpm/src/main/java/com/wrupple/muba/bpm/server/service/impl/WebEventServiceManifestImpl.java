package com.wrupple.muba.bpm.server.service.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;

import org.apache.commons.beanutils.BeanUtils;

import com.wrupple.muba.catalogs.domain.HasVanityId;
import com.wrupple.muba.catalogs.domain.WebEventTrigger;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestDescriptor;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.impl.CatalogUserTransaction;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.chain.CatalogManager;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.RequestScopedContext;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

@Singleton
public class WebEventServiceManifestImpl implements com.wrupple.muba.bpm.server.service.WebEventServiceManifest {

	private final CatalogActionRequestDescriptor descriptor;
	private final Provider<CatalogExcecutionContext> contextProvider;
	private final Provider<CatalogManager> dmp;
	private final Provider<CatalogPropertyAccesor> accessorP;
	private final Provider<CatalogTriggerInterpret> triggerDelegate;
	private final ObjectMapper mapper;
	private String serviceId;

	@Inject
	public WebEventServiceManifestImpl(ObjectMapper mapper,CatalogActionRequestDescriptor descriptor, Provider<CatalogPropertyAccesor> accessorP,Provider<CatalogTriggerInterpret> triggerDelegate,Provider<CatalogExcecutionContext> contextProvider,Provider<CatalogManager> dmp) {
		this.contextProvider = contextProvider;
		this.dmp=dmp;
		this.triggerDelegate=triggerDelegate;
		this.accessorP=accessorP;
		this.descriptor = descriptor;
		this.mapper=mapper;
	}
	
	@Override
	public String getServiceId() {
		if(serviceId==null){
			serviceId= getServiceName()+"-"+getServiceVersion();
		}
		return serviceId;
	}


	@Override
	public String[] getUrlPathParameters() {
		return new String[] { CatalogEntry.NAME_FIELD, CatalogDescriptor.DOMAIN_TOKEN, CatalogActionRequest.CATALOG_ID_PARAMETER,
				CatalogActionRequest.CATALOG_ACTION_PARAMETER, CatalogActionRequest.CATALOG_ENTRY_PARAMETER, CatalogActionRequest.FORMAT_PARAMETER };
	}

	@Override
	public Object createExcecutionContext(Object rc, String[] pathTokens, String serializedContext) throws Exception {
		
		
		/*
		 * The pourpose of web events is for Wrupple to listen to third party webhooks also see ScheduledTasksServlet
		 */
		
		RequestScopedContext requestContext = (RequestScopedContext) rc;

		String nameOfEvent = pathTokens.length > 1 ? pathTokens[firstTokenIndex] : null;

		if (nameOfEvent == null) {
			return null;
		} else {
			CatalogExcecutionContext webhookContext = contextProvider.get();

			CatalogExcecutionContext userContext = null;
			CatalogDataAccessObject<WebEventTrigger> triggerDao = dmp.get().getOrAssembleDataSource(WebEventTrigger.CATALOG, webhookContext,
					WebEventTrigger.class);
			FilterData filterData = FilterDataUtils.createSingleFieldFilter(HasVanityId.FIELD, nameOfEvent);
			filterData.setConstrained(false);
			List<WebEventTrigger> webhooks = triggerDao.read(filterData);
			if (webhooks == null || webhooks.isEmpty()) {
				throw new IllegalArgumentException("invalid webhook");
			}

			HttpServletRequest req = requestContext.getServletContext().getRequest();
			if (serializedContext == null) {
				throw new NullPointerException("event with no payload");

			} else {
				for(WebEventTrigger matchingRegistry: webhooks){
					userContext = generateUserContext(userContext,serializedContext, matchingRegistry, matchingRegistry.getDomain(), requestContext.getTransaction(), req);

				}
			}

			return userContext;
		}

	}

	private CatalogExcecutionContext generateUserContext(CatalogExcecutionContext userContext, String serializedPayload, WebEventTrigger matchingRegistry, Long domain, UserTransaction transaction,
			HttpServletRequest req) throws Exception {
		if(userContext==null){
			userContext = processAsContractClass(serializedPayload);
		}
		
		if (matchingRegistry.getExpression() != null) {
			triggerDelegate.get().configureContext(userContext, matchingRegistry, domain, (CatalogUserTransaction) transaction);
			accessorP.get().evaluate(matchingRegistry.getExpression(),null,null, userContext);
			return userContext;
		}
		return userContext;
	}

	private CatalogExcecutionContext processAsContractClass(String stream) throws IllegalAccessException, InvocationTargetException, IOException {
		Object contract = mapper.readValue(stream, CatalogActionRequestImpl.class);
		CatalogExcecutionContext context = contextProvider.get();
		BeanUtils.copyProperties(context, contract);
		return context;
	}

	@Override
	public String getServiceName() {
		return NAME;
	}

	@Override
	public String getServiceVersion() {
		return "1.0";
	}

	@Override
	public String[] getChildServicePaths() {
		return null;
	}

	@Override
	public List<? extends VegetateServiceManifest> getChildServiceManifests() {
		return null;
	}

	@Override
	public CatalogDescriptor getContractDescriptor() {
		return descriptor;
	}
}
