package com.wrupple.muba.bpm.domain;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.domain.CatalogServiceManifestImpl;

@Singleton
public class WebEventServiceManifestImpl extends CatalogServiceManifestImpl implements WebIntentServiceManifest {

	@Inject
	public WebEventServiceManifestImpl(@Named(CatalogActionRequest.CATALOG) CatalogDescriptor descriptor, CatalogRequestInterpret requestInterpret,
			CatalogEngine catalogEngine) {
		super(descriptor, requestInterpret, catalogEngine);
		String[] grammar= new String[super.getGrammar().length+1];
		grammar[0]=CatalogActionRequest.NAME_FIELD;
		for(int i =1; i < grammar.length ; i++){
			grammar[i]=super.getGrammar()[i-1];
		}
		setGrammar(grammar);
	}


	

	@Override
	public Object CatalogRequestInterpret(Object rc, String[] pathTokens, String serializedContext) throws Exception {
		
		
		
		 The pourpose of web events is for Wrupple to listen to third party webhooks also see ScheduledTasksServlet
		 
		
		RequestScopedContext requestContext = (RequestScopedContext) rc;

		String nameOfEvent = pathTokens.length > 1 ? pathTokens[firstTokenIndex] : null;

		if (nameOfEvent == null) {
			return null;
		} else {
			CatalogActionContext webhookContext = contextProvider.get();

			CatalogActionContext userContext = null;
			CatalogDataAccessObject<WebEventTrigger> triggerDao = dmp.get().getOrAssembleDataSource(WebEventTrigger.CATALOG, webhookContext,
					WebEventTrigger.class);
			FilterData filterData = FilterDataUtils.createSingleFieldFilter(HasDistinguishedName.FIELD, nameOfEvent);
			filterData.setConstrained(false);
			List<WebEventTrigger> webhooks = triggerDao.read(filterData);
			if (webhooks == null || webhooks.isEmpty()) {
				throw new IllegalArgumentException("invalid webhook");
			}

			HttpServletRequest req = requestContext.getServletContext().getRuntimeContext();
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

	private CatalogActionContext generateUserContext(CatalogActionContext userContext, String serializedPayload, WebEventTrigger matchingRegistry, Long domain, UserTransaction transaction,
			HttpServletRequest req) throws Exception {
		if(userContext==null){
			userContext = processAsContractClass(serializedPayload);
		}
		
		if (matchingRegistry.getSentence() != null) {
			triggerDelegate.get().configureContext(userContext, matchingRegistry, domain, (CatalogUserTransactionImpl) transaction);
			accessorP.get().evaluate(matchingRegistry.getSentence(),null,null, userContext);
			return userContext;
		}
		return userContext;
	}

	private CatalogActionContext processAsContractClass(String stream) throws IllegalAccessException, InvocationTargetException, IOException {
		Object contract = mapper.readValue(stream, CatalogActionRequestImpl.class);
		CatalogActionContext context = contextProvider.get();
		BeanUtils.copyProperties(context, contract);
		return context;
	}

}
