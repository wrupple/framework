package com.wrupple.muba.bootstrap.server.domain;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bootstrap.domain.ApplicationContext;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.domain.annotations.Sentence;
import com.wrupple.muba.bootstrap.domain.reserved.HasLocale;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;

@Sentence
public class ExcecutionContextImpl extends ContextBase implements ExcecutionContext {
	@Override
	public String toString() {
		return "ExcecutionContextImpl [serviceContract=" + serviceContract + ", sentence=" + Arrays.toString(sentence)
				+ ", nextIndex=" + nextIndex + "]";
	}

	private static final long serialVersionUID = 6829551639243495084L;
	public static final String SCOPED_WRITER = "muba.bootstrap.writer";
	public static final String SCOPED_BUFFER = SCOPED_WRITER + ".buffer";

	private ServiceManifest serviceManifest;
	private final SessionContext session;
	private final ApplicationContext application;
	private Object serviceContract;

	private Context serviceContext;
	private String[] sentence;
	private int nextIndex, firstWordIndex, error;

	private String locale, format, callbackFunction, id;
	private boolean scopedWriting;
	private List<String> warnings;
	private Exception caughtException;
	private UserTransaction transaction;
	private final Provider<UserTransaction> transactionProvider;
	private final ExcecutionContext parent;
	private Object result;
	private long totalResponseSize;
	private Set<ConstraintViolation<?>> constraintViolations;

	public ExcecutionContextImpl(ApplicationContext appication, SessionContext session,
			Provider<UserTransaction> transactionProvider, ExcecutionContext parent) {
		super();
		this.application = appication;
		this.transactionProvider = transactionProvider;
		this.session = session;
		this.parent = parent;
	}

	@Inject
	public ExcecutionContextImpl(ApplicationContext appication, SessionContext session,
			Provider<UserTransaction> transactionProvider) {
		this(appication, session, transactionProvider, null);
	}

	@Override
	public UserTransaction getTransaction() {
		if (transaction == null) {
			transaction = transactionProvider == null ? null : transactionProvider.get();
		}
		return transaction;
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
		// finished
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
			return getApplication().getOutputWriter();

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
	public String[] getSentence() {
		return sentence;
	}

	@Override
	public void setSentence(String[] paramTokens) {
		this.sentence = paramTokens;
	}

	@Override
	public void setNextWordIndex(int nextTokenIndex) {
		if (nextTokenIndex < firstWordIndex || nextTokenIndex >= sentence.length) {
			throw new IllegalArgumentException("out of bounds word");
		}
		this.nextIndex = nextTokenIndex;
	}

	@Override
	public int getFirstWordIndex() {
		return firstWordIndex;
	}

	@Override
	public void setFirstWordIndex(int firstTokenIndex) {
		if (nextIndex < firstWordIndex) {
			nextIndex = firstWordIndex;
		}
		this.firstWordIndex = firstTokenIndex;
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
	public boolean hasNext() {
		return nextIndex < sentence.length;
	}

	@Override
	public String next() {
		String next = sentence[nextIndex];
		nextIndex++;
		return next;
	}

	@Override
	public boolean hasPrevious() {
		return nextIndex > firstWordIndex;
	}

	@Override
	public String previous() {
		nextIndex--;
		String next = sentence[nextIndex];
		return next;
	}

	@Override
	public int nextIndex() {
		return nextIndex;
	}

	@Override
	public int previousIndex() {
		return nextIndex - 1;
	}

	@Override
	public void remove() {
	}

	@Override
	public void set(String e) {
	}

	@Override
	public void add(String e) {
	}

	@Override
	public String getCatalogType() {
		return "ExcecutionContext";
	}

	@Override
	public Object getId() {
		if (id == null) {
			//FIXME SCOPED!
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
	public void reset() {
		setNextWordIndex(this.firstWordIndex);
		serviceContext = null;
	}

	@Override
	public ExcecutionContext getParent() {
		return parent;
	}

	public ApplicationContext getApplication() {
		return application;
	}

	@Override
	public boolean process() throws Exception {
		return getApplication().getRootService().getContextProcessingCommand().execute(this);
	}

	@Override
	public <T> T getConvertedResult() {
		if (this.result == null) {
			if (this.serviceContext != this && this.serviceContext instanceof HasResult) {
				return ((HasResult) this.serviceContext).getConvertedResult();
			} else {
				return null;
			}

		} else {
			return (T) this.result;
		}

	}

	@Override
	public ExcecutionContext spawnChild() {
		return new ExcecutionContextImpl(getApplication(), getSession(), transactionProvider, this);
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
	public int hashCode() {
		//FIXME validation of context makes hascode overflow, so if were just gonna validate sentences, maybe use a separate validator?
		final int prime = 31;
		int result = 7;
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((callbackFunction == null) ? 0 : callbackFunction.hashCode());
		result = prime * result + ((caughtException == null) ? 0 : caughtException.hashCode());
		result = prime * result + ((constraintViolations == null) ? 0 : constraintViolations.hashCode());
		result = prime * result + error;
		result = prime * result + firstWordIndex;
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + nextIndex;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + (scopedWriting ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(sentence);
		result = prime * result + ((serviceContract == null) ? 0 : serviceContract.hashCode());
		result = prime * result + ((serviceManifest == null) ? 0 : serviceManifest.hashCode());
		result = prime * result + ((session == null) ? 0 : session.hashCode());
		result = prime * result + (int) (totalResponseSize ^ (totalResponseSize >>> 32));
		result = prime * result + ((transaction == null) ? 0 : transaction.hashCode());
		result = prime * result + ((transactionProvider == null) ? 0 : transactionProvider.hashCode());
		result = prime * result + ((warnings == null) ? 0 : warnings.hashCode());
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
		ExcecutionContextImpl other = (ExcecutionContextImpl) obj;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application))
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
		if (firstWordIndex != other.firstWordIndex)
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
		if (nextIndex != other.nextIndex)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (scopedWriting != other.scopedWriting)
			return false;
		if (!Arrays.equals(sentence, other.sentence))
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
		if (transactionProvider == null) {
			if (other.transactionProvider != null)
				return false;
		} else if (!transactionProvider.equals(other.transactionProvider))
			return false;
		if (warnings == null) {
			if (other.warnings != null)
				return false;
		} else if (!warnings.equals(other.warnings))
			return false;
		return true;
	}

	
}
