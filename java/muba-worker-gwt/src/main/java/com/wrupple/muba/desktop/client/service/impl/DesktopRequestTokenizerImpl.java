package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.desktop.server.domain.DesktopBuilderContext;
import com.wrupple.muba.desktop.server.service.DesktopRequestTokenizer;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Default implementation of the {@link DesktopRequestTokenizer} puts all url
 * path tokens in the context starting at the value of firstTokenIndex
 * 
 * @author japi
 *
 */
@Singleton
public class DesktopRequestTokenizerImpl implements DesktopRequestTokenizer {

	private int firstTokenIndex;
	private final Provider<DesktopBuilderContext> contextProvider;

	@Inject
	public DesktopRequestTokenizerImpl(Provider<DesktopBuilderContext> contextProvider) {
		super();
		this.contextProvider = contextProvider;
		firstTokenIndex = 1;
	}

	protected DesktopRequestTokenizerImpl(int firstTokenIndex, Provider<DesktopBuilderContext> contextProvider) {
		super();
		this.firstTokenIndex = firstTokenIndex;
		this.contextProvider = contextProvider;
	}

	@Override
	public Map<String, DesktopBuilderContext> getRequestPayload(HttpServletRequest req, String[] pathTokens)
			throws IOException,  , InstantiationException {
		DesktopBuilderContext main = contextProvider.get();
		/*
		 * String[] tokens; if(pathTokens.length>(firstTokenIndex+1)){ tokens =
		 * Arrays.copyOfRange(pathTokens, firstTokenIndex, pathTokens.length-1);
		 * }else{ tokens = null; }
		 */
		main.setFirstTokenIndex(firstTokenIndex);
		main.setNextPathToken(firstTokenIndex);
		main.setPathTokens(pathTokens);
		return Collections.singletonMap(MAIN_PARAMETER, main);
	}

	@Override
	public void setFirstTokenIndex(int i) {
		this.firstTokenIndex = i;
	}

	@Override
	public void setSingleTransaction(boolean b) {

	}

	@Override
	public String getPublicKey() {
		return null;
	}

	@Override
	public String getAccessToken() {
		return null;
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public String getCallbackFunction() {
		return null;
	}

}
