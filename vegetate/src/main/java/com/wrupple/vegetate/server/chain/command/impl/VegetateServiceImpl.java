package com.wrupple.vegetate.server.chain.command.impl;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.vegetate.domain.PeerAuthenticationToken;
import com.wrupple.vegetate.domain.VegetatePeer;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.domain.structure.HasTimestamp;
import com.wrupple.vegetate.server.chain.command.VegetateService;
import com.wrupple.vegetate.server.domain.VegetateException;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.RequestScopedContext;
import com.wrupple.vegetate.server.services.RootServiceManifest;
import com.wrupple.vegetate.shared.services.PeerManager;

public class VegetateServiceImpl extends LookupCommand implements VegetateService {

	private static final Logger log = LoggerFactory.getLogger(VegetateServiceImpl.class);

	public static final String FIRST_TOKEN_INDEX = "vegetate.firstToken", INLINE_DIVERSE_SERIVICE_CALLS = "vegetate.dinamic", QUEUE_ID = "_queued",
			MANIFEST_HOLDER = "manifestObj", THREAD = "vegetate.thread";

	private final RootServiceManifest rootManifest;

	private final ObjectMapper mapper;

	private final Provider<RequestScopedContext> rscp;

	private final Pattern splitter;

	private final boolean readCookies;

	private final boolean threading;

	private int firstTokenIndex = -1;

	private Boolean dinamic;

	/**
	 * @param factory
	 * @param rscp
	 * @param writer
	 *            like JsonVegetateResponseWriter
	 * @param rootManifest
	 * @param dictionaryName
	 *            or null to use the default command catalog where to search the
	 *            context value of the key named as the first path token of the
	 *            root service
	 */
	public VegetateServiceImpl(CatalogFactory factory, Provider<RequestScopedContext> rscp, ObjectMapper writer, RootServiceManifest rootManifest,
			@Named("vegetate.readCookies") Boolean readCookies, @Named("vegetate.multithread") Boolean threading,
			@Named("vegetate.serviceDictionary") String dictionaryName, ObjectMapper mapper) {
		super.setCatalogName(dictionaryName);
		this.mapper = mapper;
		this.threading = threading;
		super.setNameKey(rootManifest.getUrlPathParameters()[0]);
		this.rootManifest = rootManifest;
		this.splitter = Pattern.compile("/", Pattern.LITERAL);
		this.rscp = rscp;
		this.readCookies = readCookies;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		ServletWebContext ctx = (ServletWebContext) c;

		if (firstTokenIndex < 0) {
			firstTokenIndex = Integer.parseInt((String) ctx.getInitParam().get(FIRST_TOKEN_INDEX));
			log.debug("start processing path at token index {} in context path", firstTokenIndex, ctx.getContext().getContextPath());
		}
		if (dinamic == null) {
			dinamic = Boolean.parseBoolean((String) ctx.getInitParam().get(INLINE_DIVERSE_SERIVICE_CALLS));
			if (dinamic) {
				log.debug("context path {} will inline responses from any required services", ctx.getContext().getContextPath());
			} else {
				log.debug("context path {} will inline responses from single service", ctx.getContext().getContextPath());
			}

		}
		log.trace("[ INCOMMING REQUEST {} ]", ctx.getContext().getContextPath());
		RequestScopedContext requestContext = rscp.get();
		requestContext.setScopedWriting(threading);
		requestContext.setServletContext(ctx);
		HttpServletRequest req = ctx.getRequest();
		HttpServletResponse resp = requestContext.getServletContext().getResponse();
		PrintWriter writer = resp.getWriter();
		Context excecutionContext;
		Queue<ExcecutionContextThread> excecutionQueue = new LinkedList<>();
		String[] pathTokens = getAllTokens(req);
		requestContext.setFirstTokenIndex(firstTokenIndex);
		requestContext.setNextPathToken(firstTokenIndex);
		requestContext.setPathTokens(pathTokens);

		log.debug("request path tokens: {}", pathTokens);
		VegetateServiceManifest service = rootManifest.getChildServiceManifest(requestContext, pathTokens);
		log.debug("path points to service {}", service.getServiceId());
		VegetateServiceManifest dinamicPick;

		//////////////////////////////////////////////
		/////////////////// ACCESS //////////////////
		//////////////////////////////////////////////

		if (req.getParameter(PeerManager.ACCESS_TOKEN) != null) {
			requestContext.getSession().processAccessToken(req.getParameter(VegetatePeer.PUBLIC_KEY), req.getParameter(PeerManager.ACCESS_TOKEN),
					req.getParameter(PeerAuthenticationToken.MAIN_PARAMETER), req.getParameter(PeerManager.REQUEST_SALT),
					mapper.parseDate(req.getParameter(HasTimestamp.FIELD)));
		}

		Enumeration<String> names = req.getParameterNames();

		String paramV;
		long totalRequestSize;
		ExcecutionContextThread thread;

		// READ ALL PARAMETERS
		if (names.hasMoreElements()) {
			log.trace("will process multiple requests parameters");
			String[] paramVs;
			String urlParam;
			// for every request parameter
			while (names.hasMoreElements()) {
				urlParam = names.nextElement();

				if (PeerManager.CALLBACK_FUNCTION.equals(urlParam)) {
					// reserved param: callback
					requestContext.setCallbackFunction(req.getParameterValues(urlParam)[0]);

					log.debug("processing parameter {} as request principal = {}", urlParam, requestContext.getCallbackFunction());

				} else if ((dinamicPick = isServiceInvocation(urlParam, service)) != null) {

					// parameter is a serialized contextrepresentation
					paramVs = req.getParameterValues(urlParam);
					for (int i = 0; i < paramVs.length; i++) {
						paramV = paramVs[i];
						log.debug("processing parameter {} as service {}'s context = {}", urlParam, dinamicPick.getServiceId(),
								req.getParameterValues(urlParam)[0]);
						totalRequestSize = paramV.length();
						excecutionContext = (Context) dinamicPick.createExcecutionContext(requestContext, requestContext.getPathTokens(), paramV);
						excecutionContext.put(MANIFEST_HOLDER, dinamicPick);
						excecutionContext.put(VegetateService.REQUEST_SIZE_VARIABLE, totalRequestSize);
						excecutionContext.put(QUEUE_ID, urlParam);
						thread = new ExcecutionContextThread(excecutionContext, requestContext, urlParam);

						excecutionQueue.add(thread);
						if (threading) {
							thread.start();
						}

					}
				}
			}

		} else if (readCookies) {
			log.trace("no request parameters to process");
			excecutionContext = (Context) service.createExcecutionContext(requestContext, requestContext.getPathTokens(), null);
			putCookieParameters(req, excecutionContext, service);
			excecutionContext.put(MANIFEST_HOLDER, service);
			excecutionContext.put(VegetateService.REQUEST_SIZE_VARIABLE, req.getContentLengthLong());
			excecutionContext.put(QUEUE_ID, PeerAuthenticationToken.MAIN_PARAMETER);

			thread = new ExcecutionContextThread(excecutionContext, requestContext, PeerAuthenticationToken.MAIN_PARAMETER);

			excecutionQueue.add(thread);
			if (threading) {
				thread.start();
			}
			excecutionQueue.add(thread);

		}

		log.info("INITIATING RESPONSE OUTPUT WRITING");

		// when a format is specified dont alter respose
		if (requestContext.getFormat() == null) {
			resp.setContentType(mapper.getMimeType());
			resp.setCharacterEncoding(mapper.getCharacterEncoding());
			log.info("response content type = {}, charset={}", mapper.getMimeType(), mapper.getCharacterEncoding());
		}
		if (requestContext.getCallbackFunction() != null) {
			mapper.writeInvocationStart(requestContext.getCallbackFunction(), writer);
		}

		if (excecutionQueue.isEmpty()) {
			log.info("empty request queue, will print service manifest");
			// write service manifest
			mapper.writeValue(writer, service);
		} else {
			// when a format is specified dont alter respose
			if (requestContext.getFormat() == null) {
				mapper.writeContentStart(writer);

			}
			int last = excecutionQueue.size() - 1;

			int i = 0;

			Iterator<ExcecutionContextThread> iterator = excecutionQueue.iterator();
			String contextKey;
			while (iterator.hasNext()) {
				thread = iterator.next();
				excecutionContext = thread.context;

				contextKey = (String) excecutionContext.get(QUEUE_ID);
				// start to excecute context
				if (requestContext.getFormat() == null) {

					mapper.writePropertyOpen(writer, contextKey);

				}

				if (threading) {
					thread.join();
					if (thread.exception == null && thread.unrecoverable==null) {
						writer.println(requestContext.getScopedOutput(excecutionContext));
					}
				} else {
					thread.run();
				}
				if (thread.exception != null) {
					// applications may choose to hide Stack trace Info
					mapper.writeValue(writer, thread.exception);
				}
				if (thread.unrecoverable != null) {
					log.warn("irrecoverable error occured and request processing will halt ");
					writer.close();
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return PROCESSING_COMPLETE;
				}
				// end excecution context
				if (requestContext.getFormat() == null) {
					mapper.writePropertyClose(writer, contextKey, i < last);
				}
				i++;

			}

			// when a format is specified dont alter respose
			if (requestContext.getFormat() == null) {
				mapper.writeContentEnd(writer);
			}
		}

		if (requestContext.getCallbackFunction() != null) {
			// when a format is specified dont alter respose
			if (requestContext.getFormat() == null) {
				mapper.writeInvocationEnd(requestContext.getCallbackFunction(), writer);
			}
		}
		// when a format is specified dont alter respose
		if (requestContext.getFormat() == null) {
			writer.close();
		}

		log.info("RESPONSE FINISHED");

		resp.setStatus(HttpServletResponse.SC_OK);

		requestContext.end();

		return CONTINUE_PROCESSING;
	}

	private VegetateServiceManifest isServiceInvocation(String urlParam, VegetateServiceManifest urlPickedService) {
		if (dinamic) {
			int split = urlParam.lastIndexOf('_');
			if (split > 0 && split < urlParam.length() - 1) {
				if (isNumeric(urlParam.substring(0, split))) {
					return rootManifest.getChildServiceManifest(urlParam.substring(split + 1, urlParam.length()));
				} else {
					// paramenter first falf not numeric, not a context
					return null;
				}
			} else {
				// parameter does not have two required pieces Number_service
				// not a context
				return null;
			}
		} else {
			if (isNumeric(urlParam)) {
				return urlPickedService;
			} else {
				// not a context
				return null;
			}
		}

	}

	public static boolean isNumeric(final String cs) {
		if (cs.isEmpty()) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	private void putCookieParameters(HttpServletRequest request, Context context, VegetateServiceManifest manifest) {
		log.trace("writing service manifest descriptor fields from cookies");
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			Collection<String> fields = manifest.getContractDescriptor().getFields();
			if (fields != null) {
				for (String name : fields) {
					for (Cookie cookie : cookies) {
						if (name.equals(cookie.getName())) {
							log.debug("{} = {}", name, cookie.getValue());
							context.put(name, cookie.getValue());
						}
					}
				}
			}

		}
	}

	protected String[] getAllTokens(HttpServletRequest req) {
		String path = req.getRequestURI();
		String[] allTokens = splitter.split(path);
		return allTokens;
	}

	private class ExcecutionContextThread extends Thread {

		private final RequestScopedContext request;
		private final Context context;
		private final String contextKey;
		private VegetateException exception;
		private Exception unrecoverable;

		public ExcecutionContextThread(Context context, RequestScopedContext request, String contextKey) {
			this.request = request;
			this.context = context;
			this.contextKey = contextKey;
			context.put(THREAD, this);
		}

		public void run() {
			log.trace("EXCECUTING SERVICE context {}", contextKey);
			UserTransaction transaction = request.getTransaction(context);
			try {
				try {

					if (request.getSession().hasPermissionsToProcessContext(context, (VegetateServiceManifest) context.get(MANIFEST_HOLDER))) {

						log.trace("excecution permission GRANTED on {}, transaction will begin ", contextKey);
						
						if (transaction != null) {
							transaction.begin();
						}
						
						doEx(context);

					} else {
						throw new VegetateException(" Context Processing Denied ", VegetateException.DENIED, null);
					}

				} catch (VegetateException e) {
					log.error("Error while processing {}={}",contextKey,context);
					log.error("Error while processing context",  e);
					if (transaction != null) {
						transaction.rollback();
					}
					this.exception = e;
				} catch (Exception e) {
					this.unrecoverable=e;
					log.error("Unknown Error while processing {}={}",contextKey,context);
					log.error(contextKey,  e);
					if (transaction != null) {
						transaction.rollback();
					}
				} finally {
					if (transaction != null) {
						log.trace("commit transaction");
							transaction.commit();
					}

				}
			} catch (IllegalStateException | SecurityException | SystemException |RollbackException | HeuristicMixedException | HeuristicRollbackException  e1) {
				log.error("Unknown error while handling transaction",e1);
			}
		}
	}

	private void doEx(Context context) throws Exception {
		super.execute(context);
	}

}
