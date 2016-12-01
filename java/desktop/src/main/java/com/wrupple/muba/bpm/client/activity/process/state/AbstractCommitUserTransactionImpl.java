package com.wrupple.muba.bpm.client.activity.process.state;

import com.google.gwt.core.client.JsArray;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class AbstractCommitUserTransactionImpl implements CommitUserTransaction {

	protected static class EntryUpdateCallback extends DataCallback<JsCatalogEntry> {

		JsTransactionActivityContext context;
		StateTransition<JsTransactionActivityContext> onDone;

		public EntryUpdateCallback(JsTransactionActivityContext context, StateTransition<JsTransactionActivityContext> onDone) {
			super();
			this.context = context;
			this.onDone = onDone;
		}

		@Override
		public void execute() {
			context.setTargetEntryId(result.getId());
			if (context.getTargetEntryId() == null) {
				throw new IllegalArgumentException("Commited entry with no Id");
			}
			context.setUserOutput(result);
			onDone.setResultAndFinish(context);
		}

	}

	protected ProcessContextServices context;
	protected JsProcessTaskDescriptor activityDescriptor;
	private String saveTo;

	@Override
	public void start(JsTransactionActivityContext contextP, StateTransition<JsTransactionActivityContext> onDone, EventBus bus) {
		JsCatalogEntry userOutput = contextP.getUserOutput();
		if (saveTo != null) {
			// TODO certain saveTo fields are reserved, like those in CatalogAction
			GWTUtils.setAttribute(contextP, saveTo, userOutput);

			if (userOutput == null) {
				context.getDesktopManager().putPlaceParameter(saveTo, null);
			}else{
				String unencodedString;
				if (GWTUtils.isArray(userOutput)) {

					JsArray<JsCatalogEntry> out = userOutput.cast();
					if (out.length() == 0) {
						unencodedString = null;
					} else {
						StringBuilder builder = new StringBuilder(out.length()*20);
						for (int i = 0; i < out.length(); i++) {
							if (i > 0) {
								builder.append(',');
							}
							builder.append(out.get(i).getId());

						}
						unencodedString = builder.toString();
					}

				} else {
					unencodedString = userOutput.getId();
				}
				context.getDesktopManager().putPlaceParameter(saveTo, unencodedString);
			}

		}

	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context = context;
	}

	@Override
	public void setTaskDescriptor(JsProcessTaskDescriptor activityDescriptor) {
		this.activityDescriptor = activityDescriptor;

	}

	@Override
	public void setSaveTo(String task.getProducedField()) {
		this.saveTo = task.getProducedField();
	}

}
