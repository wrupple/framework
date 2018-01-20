package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.chain.command.ReadWorkerMetadata;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.HasStakeHolder;
import com.wrupple.vegetate.shared.services.PeerManager;

public class ReadWorkerMetadataImpl implements ReadWorkerMetadata {

	private PeerManager sm;

	@Inject
	public ReadWorkerMetadataImpl(PeerManager sm) {
		super();
		this.sm=sm;
	}

	@Override
	public void start(DesktopLoadingStateHolder parameter, StateTransition<DesktopLoadingStateHolder> onDone, EventBus eventBus) {

		NodeList<Element> metaTags = Document.get().getElementsByTagName("meta");

		MetaElement meta;
		String metaTagName;
		String metaContent;
		for (int i = 0; i < metaTags.getLength(); i++) {
			meta = metaTags.getItem(i).cast();
			metaTagName = meta.getName();
			metaContent = meta.getContent();
            // TODO synchronize this variables setRuntimeContext the server
            // TODO ¿a server oriented ¿Async?ServiceDictionary?
			if ("home".equals(metaTagName)) {
				String[] homeActivity ;
				if (metaContent == null || metaContent.trim().isEmpty()) {
					homeActivity = new String[]{"home"};

				}else{
					homeActivity = metaContent.split("/");
				}
				parameter.setHomeActivity(homeActivity);
			} else if(HasStakeHolder.STAKE_HOLDER_FIELD.equals(metaTagName)){
				sm.setPrincipal(GWTUtils.eval(metaContent));
			} else if ("datePattern".equals(metaTagName)) {
				DesktopLoadingStateHolder.datePattern = metaContent;
			} else if (ReadDesktopMetadata.DEFAULT_CURRENCY_CODE.equals(metaTagName)) {
				DesktopLoadingStateHolder.defaultCurrencyCode = metaContent;
			} else if(ReadDesktopMetadata.DOMAIN_SETUP_FLAG.equals(metaTagName)){
				//FIXME SETUP FLAG IS PRESENT!
				//pick a domain
				//create home
				//i dunno
			}else {
				parameter.addMetaProperty(metaTagName, metaContent);
			}
		}

		
		onDone.setResultAndFinish(parameter);
	}

}
