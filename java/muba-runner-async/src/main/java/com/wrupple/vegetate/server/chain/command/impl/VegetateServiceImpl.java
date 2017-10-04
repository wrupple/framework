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

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.domain.reserved.HasTimestamp;
import com.wrupple.muba.catalogs.domain.PeerAuthenticationToken;
import com.wrupple.muba.catalogs.domain.VegetatePeer;
import com.wrupple.muba.catalogs.domain.ServiceManifest;
import com.wrupple.muba.catalogs.server.chain.command.VegetateService;
import com.wrupple.muba.catalogs.server.domain.KnownExceptionImpl;
import com.wrupple.muba.catalogs.server.services.ObjectMapper;
import com.wrupple.muba.catalogs.server.services.RequestScopedContext;
import com.wrupple.muba.catalogs.server.services.RootServiceManifest;
import com.wrupple.muba.catalogs.shared.services.PeerManager;

public class VegetateServiceImpl  implements VegetateService {

	private static final Logger log = LoggerFactory.getLogger(VegetateServiceImpl.class);

	public static final String FIRST_TOKEN_INDEX = "vegetate.firstToken", INLINE_DIVERSE_SERIVICE_CALLS = "vegetate.dinamic", QUEUE_ID = "_queued";

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
		super.setNameKey(rootManifest.getGrammar()[0]);
		this.rootManifest = rootManifest;
		this.splitter = Pattern.compile("/", Pattern.LITERAL);
		this.rscp = rscp;
		this.readCookies = readCookies;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		ServletWebContext systemContext= (ServletWebContext) c;

		if (firstTokenIndex < 0) {
			firstTokenIndex = Integer.parseInt((String) systemContext.getInitParam().get(FIRST_TOKEN_INDEX));
			log.debug("start processing path at token index {} in context path", firstTokenIndex, systemContext.getContext().getContextPath());
		}
		if (dinamic == null) {
			dinamic = Boolean.parseBoolean((String) systemContext.getInitParam().get(INLINE_DIVERSE_SERIVICE_CALLS));
			if (dinamic) {
				log.debug("context path {} will inline responses from any required services", systemContext.getContext().getContextPath());
			} else {
				log.debug("context path {} will inline responses from single service", systemContext.getContext().getContextPath());
			}

		}
		log.trace("[ INCOMMING REQUEST {} ]", systemContext.getContext().getContextPath());
		RequestScopedContext excecutionContext= rscp.get();
		//THIS IS IMPORTANT
		excecutionContext.setScopedWriting(threading);
		excecutionContext.setServletContext(systemContext);
		HttpServletRequest req = systemContext.getRequest();
		HttpServletResponse resp = excecutionContext.getServletContext().getResponse();
		PrintWriter writer = resp.getWriter();
		Context serviceContext;
		Queue<ServiceTransactionThread> excecutionQueue = new LinkedList<>();
		String[] pathTokens = getAllTokens(req);
		excecutionContext.setFirstWordIndex(firstTokenIndex);
		excecutionContext.setNextWordIndex(firstTokenIndex);
		excecutionContext.setSentence(pathTokens);

		log.debug("request path tokens: {}", pathTokens);
		ServiceManifest service = rootManifest.getChildServiceManifest(excecutionContext, pathTokens);
		log.debug("path points to service {}", service.getServiceId());
		ServiceManifest dinamicPick;

		//////////////////////////////////////////////
		/////////////////// ACCESS //////////////////
		//////////////////////////////////////////////

		if (req.getParameter(PeerManager.ACCESS_TOKEN) != null) {
			excecutionContext.getSession().processAccessToken(req.getParameter(VegetatePeer.PUBLIC_KEY), req.getParameter(PeerManager.ACCESS_TOKEN),
					req.getParameter(PeerAuthenticationToken.MAIN_PARAMETER), req.getParameter(PeerManager.REQUEST_SALT),
					mapper.parseDate(req.getParameter(HasTimestamp.FIELD)));
		}

		Enumeration<String> names = req.getParameterNames();

		String paramV;
		long totalRequestSize;
		ServiceTransactionThread thread;

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
					excecutionContext.setCallbackFunction(req.getParameterValues(urlParam)[0]);

					log.debug("processing parameter {} as request principal = {}", urlParam, excecutionContext.getCallbackFunction());

				} else if ((dinamicPick = isServiceInvocation(urlParam, service)) != null) {

					// parameter is a serialized contextrepresentation
					paramVs = req.getParameterValues(urlParam);
					for (int i = 0; i < paramVs.length; i++) {
						paramV = paramVs[i];
						log.debug("processing parameter {} as service {}'s context = {}", urlParam, dinamicPick.getServiceId(),
								req.getParameterValues(urlParam)[0]);
						totalRequestSize = paramV.length();
						serviceContext = (Context) dinamicPick.createServiceContext(excecutionContext, paramV);
						serviceContext.put(MANIFEST_HOLDER, dinamicPick);
						serviceContext.put(VegetateService.REQUEST_SIZE_VARIABLE, totalRequestSize);
						serviceContext.put(QUEUE_ID, urlParam);
						thread = new ServiceTransactionThread(serviceContext, excecutionContext, urlParam);

						excecutionQueue.add(thread);
						if (threading) {
							thread.start();
						}

					}
				}
			}

		} else if (readCookies) {
			log.trace("no request parameters to process");
			serviceContext = (Context) service.createServiceContext(excecutionContext, null);
			putCookieParameters(req, serviceContext, service);
			serviceContext.put(MANIFEST_HOLDER, service);
			serviceContext.put(VegetateService.REQUEST_SIZE_VARIABLE, req.getContentLengthLong());
			serviceContext.put(QUEUE_ID, PeerAuthenticationToken.MAIN_PARAMETER);

			thread = new ServiceTransactionThread(serviceContext, excecutionContext, PeerAuthenticationToken.MAIN_PARAMETER);

			excecutionQueue.add(thread);
			if (threading) {
				thread.start();
			}
			excecutionQueue.add(thread);

		}

		log.trace("INITIATING RESPONSE OUTPUT WRITING");

		// when a format is specified dont alter respose
		if (excecutionContext.getFormat() == null) {
			resp.setContentType(mapper.getMimeType());
			resp.setCharacterEncoding(mapper.getCharacterEncoding());
			log.trace("response content type = {}, charset={}", mapper.getMimeType(), mapper.getCharacterEncoding());
		}
		if (excecutionContext.getCallbackFunction() != null) {
			mapper.writeInvocationStart(excecutionContext.getCallbackFunction(), writer);
		}

		if (excecutionQueue.isEmpty()) {
			log.trace("empty request queue, will print service manifest");
			// write service manifest
			mapper.writeValue(writer, service);
		} else {
			// when a format is specified dont alter respose
			if (excecutionContext.getFormat() == null) {
				mapper.writeContentStart(writer);

			}
			int last = excecutionQueue.size() - 1;

			int i = 0;

			Iterator<ServiceTransactionThread> iterator = excecutionQueue.iterator();
			String contextKey;
			while (iterator.hasNext()) {
				thread = iterator.next();
				serviceContext = thread.context;

				contextKey = (String) serviceContext.get(QUEUE_ID);
				// start to excecute context
				if (excecutionContext.getFormat() == null) {

					mapper.writePropertyOpen(writer, contextKey);

				}

				if (threading) {
					thread.join();
					if (thread.exception == null && thread.unrecoverable==null) {
						writer.println(excecutionContext.getScopedOutput(serviceContext));
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
				if (excecutionContext.getFormat() == null) {
					mapper.writePropertyClose(writer, contextKey, i < last);
				}
				i++;

			}

			// when a format is specified dont alter respose
			if (excecutionContext.getFormat() == null) {
				mapper.writeContentEnd(writer);
			}
		}

		if (excecutionContext.getCallbackFunction() != null) {
			// when a format is specified dont alter respose
			if (excecutionContext.getFormat() == null) {
				mapper.writeInvocationEnd(excecutionContext.getCallbackFunction(), writer);
			}
		}
		// when a format is specified dont alter respose
		if (excecutionContext.getFormat() == null) {
			writer.close();
		}

		log.trace("RESPONSE FINISHED");

		resp.setStatus(HttpServletResponse.SC_OK);

		excecutionContext.end();

		return CONTINUE_PROCESSING;
	}

	private ServiceManifest isServiceInvocation(String urlParam, ServiceManifest urlPickedService) {
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

	private void putCookieParameters(HttpServletRequest request, Context context, ServiceManifest manifest) {
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

	

	private void doEx(Context context) throws Exception {
		super.execute(context);
	}

}
