package com.wrupple.muba.bootstrap.server.domain;

import java.io.OutputStream;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bootstrap.domain.ApplicationContext;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.RootServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;

@Singleton
public class LocalSystemContext extends ContextBase implements ApplicationContext {

	private static final long serialVersionUID = -7144539787781019055L;
	private final RootServiceManifest rootService;
	private final PrintWriter outputWriter;
	private CatalogFactory factory;
	// use a registry method, this stays private please stop it!!
	private final String DICTIONARY = RootServiceManifest.NAME + "-interpret";

	@Inject
	public LocalSystemContext(RootServiceManifest rootService, @Named("System.out") OutputStream out,
			CatalogFactory factory) {
		super();
		this.rootService = rootService;
		this.factory = factory;
		this.outputWriter = new PrintWriter(out);
	}

	public RootServiceManifest getRootService() {
		return rootService;
	}

	public PrintWriter getOutputWriter() {
		return outputWriter;
	}

	@Override
	public CatalogFactory getDictionaryFactory() {
		return factory;
	}

	@Override
	public void registerContractInterpret(ServiceManifest manifest,
			RequestInterpret service) {
		Catalog dictionary = getDictionaryFactory().getCatalog(DICTIONARY);
		if (dictionary == null) {
			dictionary = getDictionaryFactory().getCatalog(RootServiceManifest.NAME);
			if (dictionary == null) {
				dictionary = new CatalogBase();
				getDictionaryFactory().addCatalog(RootServiceManifest.NAME, dictionary);
			}
			dictionary = new CatalogBase();
			getDictionaryFactory().addCatalog(DICTIONARY, dictionary);

		}
		dictionary.addCommand(manifest.getServiceId(), service);

	}

	@Override
	public void registerService( ServiceManifest manifest, Command service) {
		Catalog dictionary = getDictionaryFactory().getCatalog(RootServiceManifest.NAME);
		if (dictionary == null) {
			dictionary = getDictionaryFactory().getCatalog(DICTIONARY);
			if (dictionary == null) {
				dictionary = new CatalogBase();
				getDictionaryFactory().addCatalog(DICTIONARY, dictionary);
			}

			dictionary = new CatalogBase();
			getDictionaryFactory().addCatalog(RootServiceManifest.NAME, dictionary);

		}
		dictionary.addCommand(manifest.getServiceId(), service);
		getRootService().register(manifest);
	}

	@Override
	public RequestInterpret getRequestInterpret(ExcecutionContext req) {
		return (RequestInterpret) getDictionaryFactory().getCatalog(DICTIONARY).getCommand(req.getServiceManifest().getServiceId());
	}
}
