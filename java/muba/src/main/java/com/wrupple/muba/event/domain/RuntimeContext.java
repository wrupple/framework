package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.reserved.*;
import org.apache.commons.chain.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

public interface RuntimeContext extends Context, HasValidations, HasLocale, ListIterator<String>, CatalogKey,
		HasParentValue<Object,RuntimeContext>, HasResult<Object> ,HasResults<Object> {

    int Unauthorized = 401;
    int PaymentRequired = 402;

	String FORMAT = "format";

	EventBus getEventBus();

    ContainerContext getSession();

	void reset();

	void end();

	void setScopedWriting(boolean b);

	PrintWriter getScopedWriter(Context c) throws IOException;

	CharSequence getScopedOutput(Context serviceContext);

	void setFormat(String defaultFormat);

    List<String> getSentence();

    void setSentence(List<String> pathTokens);

	String getFormat();

	void setLocale(String locale);

	//// BPMPeer client =
	//// BusinessEventSuscriptionManager.getPeerByEncodedId(remembermeToken);
    String getCallbackFunction();

    void setCallbackFunction(String callbackFunction);

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

    long getTotalResponseSize();

    void setTotalResponseSize(long length);

	void setServiceManifest(ServiceManifest manifest);

	ServiceManifest getServiceManifest();

	<T extends Context> T getServiceContext();

	void setServiceContext(Context context);

	void setResult(Object result);

    String deduceLocale(Context domainContext);

	void setSentence(String... words);

    boolean process() throws Exception;



	RuntimeContext spawnChild();


	TransactionHistory getTransactionHistory();
}
