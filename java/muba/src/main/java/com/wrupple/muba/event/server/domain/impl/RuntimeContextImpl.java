package com.wrupple.muba.event.server.domain.impl;

import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.domain.TransactionHistory;
import com.wrupple.muba.event.domain.annotations.Sentence;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.event.domain.reserved.HasLocale;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.event.domain.reserved.HasResults;
import org.apache.commons.chain.Context;

import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@Sentence
public class RuntimeContextImpl extends AbstractYieldContext implements RuntimeContext {
	@Override
	public String toString() {
		return "RuntimeContextImpl [serviceContract=" + serviceContract + ", sentence=" + sentence + "]";
	}

	private static final long serialVersionUID = 6829551639243495084L;
	public static final String SCOPED_WRITER = "muba.event.writer";
	public static final String SCOPED_BUFFER = SCOPED_WRITER + ".buffer";


	private int  error;

	private ServiceManifest serviceManifest;
	private Object serviceContract;

	private Context serviceContext;
    private final SessionContext session;
    private final ServiceBus serviceBus;

	private String locale, format, callbackFunction, id;
	private boolean scopedWriting;
	private List<String> warnings;
	private Exception caughtException;
	private UserTransaction transaction;
	private final RuntimeContext parentValue;
	private Object result;
	private long totalResponseSize;
	private Set<ConstraintViolation<?>> constraintViolations;

    public RuntimeContextImpl(ServiceBus appication, SessionContext session, RuntimeContext parent) {
        super();
		this.serviceBus = appication;
		this.session = session;
		this.parentValue = parent;
	}

	@Override
	public boolean process() throws Exception {
		return getServiceBus().resume(this);
	}


	@Override
	public void forward() throws Exception {
		setServiceContract(null);
		setServiceContext(null);
		process();
	}

	@Override
	public void reset() {
super.reset();
serviceContext = null;
	}

    public RuntimeContextImpl(ServiceBus appication, SessionContext session) {
        this( appication, session, null);
	}

	@Override
	public String deduceLocale(Context domainContext) {

		// excecution
		if (getLocale() == null || HasLocale.LOCALE_FIELD.equals(getLocale())) {
            SessionContext session = getSession();
            String locale = (String) session.get(HasLocale.LOCALE_FIELD);

			// session
			if (locale == null || HasLocale.LOCALE_FIELD.equals(locale)) {
				locale = (String) (domainContext == null ? null : domainContext.get(HasLocale.LOCALE_FIELD));

				// workgroup
				if (locale == null || HasLocale.LOCALE_FIELD.equals(locale)) {
					// system default
					return System.getProperty("user.language", "en_us");
				} else {
					return locale;
				}

			} else {
				return locale;
			}
		} else {
			return getLocale();
		}

	}

	@Override
	public void addWarning(String string) {
		if (warnings == null) {
			warnings = new ArrayList<>(5);
		}
		warnings.add(string);
	}

	@Override
	public List<String> resetWarnings() {
		if (warnings == null) {
			return null;
		} else {
			List<String> r = Collections.unmodifiableList(warnings);
			warnings.clear();
			return r;
		}
	}

	@Override
	public void end() {
		// do sometihing when processing ends, it doesnt necesarly mean it's
		// finished, it may never be required to actually finish like if a constraint violation was found
		// TODO release resources
	}

	@Override
	public PrintWriter getScopedWriter(Context c) throws IOException {
		if (scopedWriting) {
			PrintWriter r = (PrintWriter) c.get(SCOPED_WRITER);
			if (r == null) {
				StringWriter w = new StringWriter();
				r = new PrintWriter(w);
				c.put(SCOPED_WRITER, r);
				c.put(SCOPED_BUFFER, w);
			}
			return r;
		} else {
			return getServiceBus().getOutputWriter();

		}

	}

	@Override
	public CharSequence getScopedOutput(Context serviceContext) {
		if (scopedWriting) {
			StringWriter writer = (StringWriter) serviceContext.get(SCOPED_BUFFER);
			return writer.getBuffer().toString();
		} else {
			return null;

		}

	}

	@Override
	public String getLocale() {
		return locale;
	}

	@Override
	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public String getCallbackFunction() {
		return callbackFunction;
	}

	@Override
	public void setCallbackFunction(String callbackFunction) {
		this.callbackFunction = callbackFunction;
	}

	@Override
	public void setScopedWriting(boolean scopedWriting) {
		this.scopedWriting = scopedWriting;
	}



	@Override
	public void setSentence(String... paramTokens) {
		sentence = Arrays.asList(paramTokens);
	}
	@Override
    public SessionContext getSession() {
        return session;
	}

	@Override
	public void setCaughtException(Exception e) {
		this.caughtException = e;
	}

	@Override
	public Exception getCaughtException() {
		return caughtException;
	}

	@Override
	public String getCatalogType() {
		return "RuntimeContext";
	}

	@Override
	public Object getId() {
		if (id == null) {
			// FIXME SCOPED!
			Thread t = Thread.currentThread();
			id = t.getName();
		}
		return id;
	}

	@Override
	public Object getServiceContract() {
		return serviceContract;
	}

	public void setServiceContract(Object serviceContract) {
		this.serviceContract = serviceContract;
	}

	@Override
	public ServiceManifest getServiceManifest() {
		return serviceManifest;
	}

	@Override
	public void setServiceManifest(ServiceManifest serviceManifest) {
		this.serviceManifest = serviceManifest;
	}

	@Override
	public Context getServiceContext() {
		return serviceContext;
	}

	@Override
	public void setServiceContext(Context serviceContext) {
		this.serviceContext = serviceContext;
	}


	@Override
	public RuntimeContext getParentValue() {
		return parentValue;
	}

	public ServiceBus getServiceBus() {
		return serviceBus;
	}

	@Override
	public <T> T getConvertedResult() {
		if (this.result == null) {
			if (this.serviceContext != this && this.serviceContext instanceof HasResult) {
				return ((HasResult<T>) this.serviceContext).getConvertedResult();
			} else {
				return (T) this.result;
			}

		} else {
			return (T) this.result;
		}
	}

    @Override
    public <T> List<T> getResults() {
        if (this.serviceContext != this && this.serviceContext instanceof HasResults) {
            return ((HasResults<T>) this.serviceContext).getResults();

        } else {
            if(result instanceof  List){
            	return (List<T>) result;
			}else{
            	return Collections.singletonList((T) result);
			}
        }
    }

    @Override
    public <T> void setResults(List<T> discriminated) {

    }

	@Override
	public RuntimeContext spawnChild() {
		return new RuntimeContextImpl( getServiceBus(), getSession(), this);
	}

	@Override
	public RuntimeContext getRootAncestor() {
		return CatalogEntryImpl.getRootAncestor(this);
	}

	@Override
	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public void addConstraintViolation(ConstraintViolation<?> string) {
		if (constraintViolations == null) {
			constraintViolations = new HashSet<>(4);
		}
		constraintViolations.add(string);
	}

	@Override
	public void addAllConstraintViolation(Collection<ConstraintViolation<?>> violations) {
		if (constraintViolations == null) {
			constraintViolations = new HashSet<>(4);
		}
		constraintViolations.addAll(violations);
	}

	public void setConstraintViolations(Set<ConstraintViolation<?>> aggregate) {
		this.constraintViolations = aggregate;
	}

	public Set<ConstraintViolation<?>> getConstraintViolations() {
		return constraintViolations;
	}

	public void setTotalResponseSize(long lenght) {
		this.totalResponseSize = lenght;
	}

	public long getTotalResponseSize() {
		return totalResponseSize;
	}

	public int getError() {
		return error;
	}

	@Override
	public void serErrot(int error) {
		this.error = error;
	}

	@Override
	public Object getResult() {
		return getConvertedResult();
	}

	@Override
	public void setSentence(List<String> pathTokens) {
		if(pathTokens==null){
			throw new NullPointerException("Cannot explicitly set a null sentence");
		}
		this.sentence = pathTokens;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime;
		result = prime * result + ((serviceBus == null) ? 0 : serviceBus.hashCode());
		result = prime * result + ((callbackFunction == null) ? 0 : callbackFunction.hashCode());
		result = prime * result + ((caughtException == null) ? 0 : caughtException.hashCode());
		result = prime * result + ((constraintViolations == null) ? 0 : constraintViolations.hashCode());
		result = prime * result + error;
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + (scopedWriting ? 1231 : 1237);
		result = prime * result + ((sentence == null) ? 0 : sentence.hashCode());
		result = prime * result + ((serviceContract == null) ? 0 : serviceContract.hashCode());
		result = prime * result + ((serviceManifest == null) ? 0 : serviceManifest.hashCode());
		result = prime * result + ((session == null) ? 0 : session.hashCode());
		result = prime * result + (int) (totalResponseSize ^ (totalResponseSize >>> 32));
		result = prime * result + ((transaction == null) ? 0 : transaction.hashCode());
		result = prime * result + ((warnings == null) ? 0 : warnings.hashCode());
		result = prime * result + ((wordIterator == null) ? 0 : wordIterator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuntimeContextImpl other = (RuntimeContextImpl) obj;
		if (serviceBus == null) {
			if (other.serviceBus != null)
				return false;
		} else if (!serviceBus.equals(other.serviceBus))
			return false;
		if (callbackFunction == null) {
			if (other.callbackFunction != null)
				return false;
		} else if (!callbackFunction.equals(other.callbackFunction))
			return false;
		if (caughtException == null) {
			if (other.caughtException != null)
				return false;
		} else if (!caughtException.equals(other.caughtException))
			return false;
		if (constraintViolations == null) {
			if (other.constraintViolations != null)
				return false;
		} else if (!constraintViolations.equals(other.constraintViolations))
			return false;
		if (error != other.error)
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		if (parentValue == null) {
			if (other.parentValue != null)
				return false;
		} else if (!parentValue.equals(other.parentValue))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (scopedWriting != other.scopedWriting)
			return false;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if (!sentence.equals(other.sentence))
			return false;
		if (serviceContract == null) {
			if (other.serviceContract != null)
				return false;
		} else if (!serviceContract.equals(other.serviceContract))
			return false;
		if (serviceManifest == null) {
			if (other.serviceManifest != null)
				return false;
		} else if (!serviceManifest.equals(other.serviceManifest))
			return false;
		if (session == null) {
			if (other.session != null)
				return false;
		} else if (!session.equals(other.session))
			return false;
		if (totalResponseSize != other.totalResponseSize)
			return false;
		if (transaction == null) {
			if (other.transaction != null)
				return false;
		} else if (!transaction.equals(other.transaction))
			return false;
		if (warnings == null) {
			if (other.warnings != null)
				return false;
		} else if (!warnings.equals(other.warnings))
			return false;
		if (wordIterator == null) {
			if (other.wordIterator != null)
				return false;
		} else if (!wordIterator.equals(other.wordIterator))
			return false;
		return true;
	}


	@Override
	public Object getParent() {
		return parentValue.getId();
	}


	@Override
	public TransactionHistory getTransactionHistory() {
		RuntimeContext root = getRootAncestor();
		if (root == this) {
			return assertTransaction();
		} else {
			return root.getTransactionHistory();
		}
	}


	private TransactionHistory assertTransaction() {
		if (transaction == null) {
            transaction = new CatalogUserTransactionImpl(null/*TODO*/);
        }
		return (TransactionHistory) transaction;
	}


}
