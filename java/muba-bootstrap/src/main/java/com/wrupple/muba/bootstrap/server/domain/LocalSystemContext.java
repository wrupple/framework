package com.wrupple.muba.bootstrap.server.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bootstrap.server.service.EventBus;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;

@Singleton
public class LocalSystemContext extends ContextBase implements SystemContext {

	private static final long serialVersionUID = -7144539787781019055L;
	private final RootServiceManifest rootService;
	private final PrintWriter outputWriter;
    private final OutputStream out;
    private final InputStream in;
    private CatalogFactory factory;
	// use a registry method, this stays private please stop it!!
	private final String DICTIONARY = RootServiceManifest.NAME + "-interpret";
	private final EventBus intentInterpret;

	@Inject
	public LocalSystemContext(RootServiceManifest rootService, @Named("System.out") OutputStream out, @Named("System.in") InputStream in,
                              CatalogFactory factory, EventBus intentInterpret) {
		super();
		this.rootService = rootService;
		this.factory = factory;
		this.out=out;
		this.in = in;
		this.outputWriter = new PrintWriter(out);
        this.intentInterpret = intentInterpret;
    }

	public RootServiceManifest getRootService() {
		return rootService;
	}

    @Override
    public OutputStream getOutput() {
        return out;
    }

    @Override
    public InputStream getInput() {
        return in;
    }

    public PrintWriter getOutputWriter() {
		return outputWriter;
	}

	@Override
	public CatalogFactory getDictionaryFactory() {
		return factory;
	}

	@Override
	public void registerService(ServiceManifest manifest, Command service, RequestInterpret contractInterpret) {
		registerService(manifest,service);
		registerContractInterpret(manifest,contractInterpret);
	}


	private void registerContractInterpret(ServiceManifest manifest,
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
	public RequestInterpret getRequestInterpret(RuntimeContext req) {
		return (RequestInterpret) getDictionaryFactory().getCatalog(DICTIONARY).getCommand(req.getServiceManifest().getServiceId());
	}

    @Override
    public EventBus getIntentInterpret() {
        return intentInterpret;
    }
}
