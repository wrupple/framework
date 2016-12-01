package com.wrupple.muba.bootstrap.domain;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

import javax.transaction.UserTransaction;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.reserved.HasLocale;
import com.wrupple.muba.bootstrap.domain.reserved.HasParent;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;

public interface ExcecutionContext extends Context,HasValidations,HasLocale,ListIterator<String>,CatalogKey , HasParent<ExcecutionContext>,HasResult{

	
	
	String FORMAT = "format";
	
	ApplicationContext getApplication();
	SessionContext getSession();
	UserTransaction getTransaction();
	
	void reset();
	void end();
	boolean process() throws Exception;
	void setScopedWriting(boolean b);
	PrintWriter getScopedWriter(Context c) throws IOException;
	CharSequence getScopedOutput(Context serviceContext);
	
	
	void setFormat(String defaultFormat);
	public String[] getSentence();
	public void setSentence(String... pathTokens);
	String getFormat();
	void setLocale(String locale);
	////BPMPeer client = BusinessEventSuscriptionManager.getPeerByEncodedId(remembermeToken);
	public String getCallbackFunction() ;
	public void setCallbackFunction(String callbackFunction);
	
	Object getServiceContract();
	void setServiceContract(Object serviceInvocationContract);


	/*
	 * 
	 * 
	 */
	
	void setNextWordIndex(int i);
	public int getFirstWordIndex();
	public void setFirstWordIndex(int firstTokenIndex);

	List<String> resetWarnings();
	
	/*
	 * 
	 * 
	 */
	
	void setCaughtException(Exception e);
	Exception getCaughtException();
	int getError();
	void serErrot(int error);
	public void setTotalResponseSize(long length);
	public long getTotalResponseSize();
	void setServiceManifest(ServiceManifest manifest);
	ServiceManifest getServiceManifest();
	<T extends Context> T getServiceContext();
	void setServiceContext(Context context);
	
	void setResult(Object result);
	public String deduceLocale(Context domainContext);


}