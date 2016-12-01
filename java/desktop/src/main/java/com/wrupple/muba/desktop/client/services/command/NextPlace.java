package com.wrupple.muba.desktop.client.services.command;

import com.wrupple.muba.desktop.client.services.logic.OutputHandler;
import com.wrupple.muba.desktop.shared.services.UrlParser;

public interface NextPlace extends OutputHandler {
	final String COMMAND = UrlParser.NEXT_APPLICATION_ITEM;
}
