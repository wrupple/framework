package com.wrupple.vegetate.server.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.transaction.UserTransaction;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.servlet.ServletWebContext;

import com.wrupple.vegetate.domain.HasLocale;
import com.wrupple.vegetate.server.chain.CatalogManager;
import com.wrupple.vegetate.server.domain.DomainContext;

public interface RequestScopedContext extends Context,HasLocale {

	final String SERVICE_FIELD = "service";
	String FORMAT = "format";
	
	
	void setFormat(String defaultFormat);
	String getFormat();
	void setLocale(String locale);
	////BPMPeer client = BusinessEventSuscriptionManager.getPeerByEncodedId(remembermeToken);
	public String getCallbackFunction() ;
	public void setCallbackFunction(String callbackFunction);
	
	
	ServletWebContext getServletContext();
	void setServletContext(ServletWebContext ctx);
	SessionContext getSession();


	CatalogManager getStorageManager();
	PeerManager getPeerManager();

	/**
	 * @param c
	 * @return
	 */
	public UserTransaction getTransaction(Context c /* always use the root ancestor of the catalog context*/);
	public String deduceLocale(DomainContext domainContext);
	public String[] getPathTokens();
	public void setPathTokens(String[] pathTokens);
	public int getNextPathToken();
	public void setNextPathToken(int nextPathToken);
	public int getFirstTokenIndex();
	public void setFirstTokenIndex(int firstTokenIndex);
	void addWarning(String string);
	List<String> resetWarnings();
	
	void end();
	void setScopedWriting(boolean b);
	PrintWriter getScopedWriter(Context c) throws IOException;
	CharSequence getScopedOutput(Context serviceContext);

}
