package com.wrupple.muba.event.domain;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.reserved.*;
import org.apache.commons.chain.Context;

public interface RuntimeContext extends Context, HasValidations, HasLocale, ListIterator<String>, CatalogKey,
		HasParentValue<Object,RuntimeContext>, HasResult<Object> ,HasResults<Object> {

	final int Unauthorized = 401;
	final int PaymentRequired = 402;

	String FORMAT = "format";

	EventBus getEventBus();

	SessionContext getSession();

	void reset();

	void end();

	void setScopedWriting(boolean b);

	PrintWriter getScopedWriter(Context c) throws IOException;

	CharSequence getScopedOutput(Context serviceContext);

	void setFormat(String defaultFormat);

	public List<String> getSentence();

	public void setSentence(List<String> pathTokens);

	String getFormat();

	void setLocale(String locale);

	//// BPMPeer client =
	//// BusinessEventSuscriptionManager.getPeerByEncodedId(remembermeToken);
	public String getCallbackFunction();

	public void setCallbackFunction(String callbackFunction);

	Object getServiceContract();

	void setServiceContract(Object serviceInvocationContract);

	/*
	 * 
	 * 
	 */

	void setNextWordIndex(int i);


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

	void setSentence(String... words);

    boolean process() throws Exception;



	RuntimeContext spawnChild();


	TransactionHistory getTransactionHistory();
}
