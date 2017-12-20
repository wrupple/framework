package com.wrupple.muba.desktop.server;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestProcessor;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.desktop.server.domain.ContextCompatibleCatalogActionRequest;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.VegetateServlet;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.RequestScopedContext;
import com.wrupple.vegetate.server.services.ServletRequestTokenizer;
import com.wrupple.vegetate.server.services.SessionContext;
import com.wrupple.vegetate.server.services.impl.JsonVegetateResponseWriter;
import com.wrupple.vegetate.server.services.impl.ServletRequestTokenizerImpl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class WruppleCatalogServlet extends VegetateServlet<CatalogExcecutionContext> {

	private static final long serialVersionUID = 2905974079877379201L;

	@Inject
	public WruppleCatalogServlet(Provider<SessionContext> usipp, Provider<CatalogExcecutionContext> contextProvider, Provider<CatalogRequestProcessor> catalog,
			ObjectMapper mapper, CatalogServiceManifest manifest, TokenizerProvider tp, Provider<RequestScopedContext> rscc) {
		super(new JsonVegetateResponseWriter<CatalogExcecutionContext>(mapper), tp, catalog, usipp, manifest, rscc);
		tp.contextProvider = contextProvider;
		tp.manifest = manifest;
		tp.mapper = mapper;
	}

	protected WruppleCatalogServlet(int firstTokenIndex, Provider<SessionContext> usipp, Provider<CatalogExcecutionContext> contextProvider,
			Provider<CatalogRequestProcessor> catalog, ObjectMapper mapper, CatalogServiceManifest manifest, TokenizerProvider tp,
			Provider<RequestScopedContext> rscc) {
		super(new JsonVegetateResponseWriter<CatalogExcecutionContext>(mapper), tp, catalog, usipp, manifest, rscc);
		tp.contextProvider = contextProvider;
		tp.manifest = manifest;
		tp.mapper = mapper;
		tp.firstTokenIndex = firstTokenIndex;
	}

	protected WruppleCatalogServlet(Provider<SessionContext> usipp, Provider<CatalogExcecutionContext> contextProvider,
			Provider<CatalogRequestProcessor> catalog, ObjectMapper mapper, CatalogServiceManifest manifest,
			Provider<? extends ServletRequestTokenizer<CatalogExcecutionContext>> tp, Provider<RequestScopedContext> rscc) {
		super(new JsonVegetateResponseWriter<CatalogExcecutionContext>(mapper), (Provider) tp, catalog, usipp, manifest, rscc);
	}

	static class TokenizerProvider implements Provider<ServletRequestTokenizer<CatalogExcecutionContext>> {

		private VegetateServiceManifest manifest;
		private Provider<CatalogExcecutionContext> contextProvider;
		private ObjectMapper mapper;
		private int firstTokenIndex = 2;

		@Override
		public ServletRequestTokenizer<CatalogExcecutionContext> get() {
			ServletRequestTokenizerImpl<CatalogExcecutionContext> requestTokenizer = new ServletRequestTokenizerImpl<CatalogExcecutionContext>(
					ContextCompatibleCatalogActionRequest.class, mapper, contextProvider, manifest);
			requestTokenizer.setFirstTokenIndex(firstTokenIndex);
			return requestTokenizer;
		}

	}
}
