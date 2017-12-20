package com.wrupple.muba.bpm.client.activity.process.state;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.desktop.shared.services.UrlParser;

import java.util.List;

public class AbstractStartUserTransaction implements StartUserTransaction {

	protected ProcessContextServices contextServices;
	protected JsProcessTaskDescriptor taskDescriptor;
	protected final CatalogPlaceInterpret placeInterpret;

	@Inject
	public AbstractStartUserTransaction(CatalogPlaceInterpret placeInterpret) {
		super();
		this.placeInterpret = placeInterpret;
	}

	@Override
	public void start(JsTransactionApplicationContext context, StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
		context.setTaskDescriptor(taskDescriptor);
		context.setCurrentTaskIndex(context.getCurrentTaskIndex() + 1);
		DesktopPlace currentPlace = (DesktopPlace) contextServices.getPlaceController().getWhere();
		String catalog = taskDescriptor.getCatalogId();
		if (catalog == null) {
			catalog = GWTUtils.getAttribute(context, CatalogActionRequest.CATALOG_ID_PARAMETER);
			if (catalog == null) {

				catalog = placeInterpret.getPlaceCatalog(currentPlace);
			}
			assert catalog != null : "User Interaction Task with no target catalog";
			taskDescriptor.setCatalogId(catalog);
		}

		List<String> taskTokens = taskDescriptor.getUrlTokens();

		if (taskTokens == null && (CatalogActionRequest.READ_ACTION.equals(taskDescriptor.getTransactionType())
				|| CatalogActionRequest.WRITE_ACTION.equals(taskDescriptor.getTransactionType()))) {
			taskTokens = UrlParser.DEFAULT_FORM_TASK_TOKENS;
		}
		currentPlace.setTaskTokens(taskTokens);
		if (taskTokens != null) {
			String[] tokens = currentPlace.getTokens();
			int tokenIndex = currentPlace.getLastActivityToken();
			// start processing the next token
			tokenIndex = tokenIndex + 1;

			if (tokenIndex < tokens.length) {
				// hay al menos un token sin procesar
				int taskIndex = context.getCurrentTaskIndex();
				String token = tokens[tokenIndex];
				if (taskIndex == 0) {
					if (token.equals(taskDescriptor.getId()) || token.equalsIgnoreCase(taskDescriptor.getVanityId())) {
						// el primer token es el identificador de tarea
						tokenIndex = tokenIndex + 1;
						// FIXME y si resulta ser que coincide el id numérico de
						// la tarea con el Id de otra cosa?
					} else {
						// do nothing, this token may be a task variable
					}
				} else {
					tokenIndex = tokenIndex + 1;
					// el primer token DEBERÍA ser identificador de tarea, si no
					// lo es hay que corregirlo
					if (token.equals(taskDescriptor.getId()) || token.equalsIgnoreCase(taskDescriptor.getVanityId())) {
						// do nothing,el primer token es el identificador de
						// tarea
					} else {
						// we correct the issue of this token not representing
						// the task just BY REPLACING with the correct value
						if (taskDescriptor.getVanityId() == null) {
							tokens[tokenIndex] = taskDescriptor.getId();
						} else {
							tokens[tokenIndex] = taskDescriptor.getVanityId();
						}
					}
				}

				// iterate over tokens comparing them with declared token names
				// and save them
				for (int i = 0; i < taskTokens.size(); i++) {
					if ((tokenIndex + i) < tokens.length) {
						currentPlace.setProperty(taskTokens.get(i), tokens[tokenIndex + i]);
					}
				}

				currentPlace.setTokens(tokens);
				contextServices.getDesktopManager().updatePlace(currentPlace);
			}
		}

	}

	@Override
	public void setTaskDescriptor(JsProcessTaskDescriptor activityDescriptor) {
		this.taskDescriptor = activityDescriptor;
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.contextServices = context;
	}

}
