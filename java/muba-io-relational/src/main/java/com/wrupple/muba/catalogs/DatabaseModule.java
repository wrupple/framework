package com.wrupple.muba.catalogs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.chain.CatalogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntityImpl;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionTriggerHandler;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDeleteTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.ExplicitDataJoin;
import com.wrupple.muba.catalogs.server.chain.command.ImplicitDataJoin;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogCreateTransactionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogDeleteTransactionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogReadTransactionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogUpdateTransactionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CompleteCatalogGraphImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.ExplicitDataJoinImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.ImplicitDataJoinImpl;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.Deleters;
import com.wrupple.muba.catalogs.server.service.EntryCreators;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import com.wrupple.muba.catalogs.server.service.Writers;
import com.wrupple.muba.catalogs.server.service.impl.CatalogActionTriggerHandlerImpl;
import com.wrupple.muba.catalogs.server.service.impl.CatalogEvaluationDelegateImpl;
import com.wrupple.muba.catalogs.server.service.impl.CatalogTriggerInterpretImpl;
import com.wrupple.muba.catalogs.server.service.impl.DeletersImpl;
import com.wrupple.muba.catalogs.server.service.impl.EntryCreatorsImpl;
import com.wrupple.muba.catalogs.server.service.impl.LargeStringFieldDataAccessObjectImpl;
import com.wrupple.muba.catalogs.server.service.impl.PrimaryKeyReadersImpl;
import com.wrupple.muba.catalogs.server.service.impl.QueryReadersImpl;
import com.wrupple.muba.catalogs.server.service.impl.WritersImpl;
import com.wrupple.muba.catalogs.shared.services.PrimaryKeyEncodingService;
import com.wrupple.muba.catalogs.shared.services.impl.PrimaryKeyEncodingServiceImpl;

public class DatabaseModule extends AbstractModule {

	@Override
	protected void configure() {
		/*
		 * workarounds / replacement classes
		 */
		ConvertUtils.register(new LongConverter(null), Long.class);
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		bind(CatalogFactory.class).toInstance(CatalogFactory.getInstance());
		String rawPattern = "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}";
		Pattern pattern = Pattern.compile(rawPattern);
		bind(Pattern.class).annotatedWith(Names.named("template.pattern")).toInstance(pattern);

		// 2014-01-18T00:35:03.463Z
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		bind(String.class).annotatedWith(Names.named("catalog.datePattern")).toInstance(datePattern);
		bind(DateFormat.class).toInstance(new SimpleDateFormat(datePattern));
		bind(String.class).annotatedWith(Names.named("catalog.domainField")).toInstance("domain");
		bind(Boolean.class).annotatedWith(Names.named("catalog.createablePrimaryKeys")).toInstance(false);

		bind(Integer.class).annotatedWith(Names.named("catalog.read.preloadCatalogGraph")).toInstance(0);

		bind(PersistentCatalogEntity.class).to(PersistentCatalogEntityImpl.class);

		/*
		 * Dictionaries
		 */
		bind(EntryCreators.class).to(EntryCreatorsImpl.class);
		bind(PrimaryKeyReaders.class).to(PrimaryKeyReadersImpl.class);
		bind(QueryReaders.class).to(QueryReadersImpl.class);
		bind(Writers.class).to(WritersImpl.class);
		bind(Deleters.class).to(DeletersImpl.class);

		/*
		 * Commands
		 */

		bind(CatalogCreateTransaction.class).to(CatalogCreateTransactionImpl.class);
		bind(CatalogReadTransaction.class).to(CatalogReadTransactionImpl.class);
		bind(CatalogUpdateTransaction.class).to(CatalogUpdateTransactionImpl.class);
		bind(CatalogDeleteTransaction.class).to(CatalogDeleteTransactionImpl.class);
		bind(CatalogActionTriggerHandler.class).to(CatalogActionTriggerHandlerImpl.class);

		/*
		 * Services
		 */
		bind(CatalogEvaluationDelegate.class).to(CatalogEvaluationDelegateImpl.class);
		bind(CompleteCatalogGraph.class).to(CompleteCatalogGraphImpl.class);
		bind(ExplicitDataJoin.class).to(ExplicitDataJoinImpl.class);
		bind(ImplicitDataJoin.class).to(ImplicitDataJoinImpl.class);

		bind(PrimaryKeyEncodingService.class).to(PrimaryKeyEncodingServiceImpl.class);
		bind(CatalogTriggerInterpret.class).to(CatalogTriggerInterpretImpl.class);

		bind(LargeStringFieldDataAccessObject.class).to(LargeStringFieldDataAccessObjectImpl.class);
	}

}
