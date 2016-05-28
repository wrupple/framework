package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.validation.ConstraintViolation;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.PublishEvents.CatalogBroadcastData;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogMetadataReader;
import com.wrupple.muba.catalogs.server.domain.CatalogBroadcastDataImpl;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler.Trigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.chain.command.CatalogCommand;

@Singleton
public final class CatalogCommandImpl extends LookupCommand  implements CatalogCommand {

	protected static final Logger log = LoggerFactory.getLogger(CatalogCommandImpl.class);
	protected final DatabasePlugin database;
	protected final CatalogPropertyAccesor accessor;
	private final Provider<CatalogActionTriggerHandler> trigererProvider;
	private final Provider<CatalogResultCache> cacheProvider;
	private final CatalogQueryRewriter queryRewriter;
	private final CatalogMetadataReader mdataReader;

	static private final String CONTEXT_PREFIX_DAO = "DAO";

	public CatalogCommandImpl(CatalogFactory commandDictionatyFactory,CatalogMetadataReader mdataReader,CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider,
			Provider<CatalogActionTriggerHandler> trigererProvider, CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(commandDictionatyFactory);
		this.queryRewriter = queryRewriter;
		this.cacheProvider = cacheProvider;
		this.trigererProvider = trigererProvider;
		this.accessor = accessor;
		this.mdataReader=mdataReader;
		this.database = daoFactory;
		super.setCatalogName(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
		super.setNameKey(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
	}


	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		
		if (CatalogActionRequest.LIST_ACTION_TOKEN.equals(context.getCatalog())) {
			
			return mdataReader.execute(context);
		}
		
		Set<ConstraintViolation<?>> violations = context.getConstraintViolations();
		if (violations == null || violations.isEmpty()) {
			super.execute(context);

		}
		return CONTINUE_PROCESSING;
	}

	@Override
	public CatalogEntry create(CatalogEntry uncatalog, CatalogExcecutionContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<CatalogEntry> read(FilterData filter, CatalogExcecutionContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public CatalogEntry read(Object targetEntryId, CatalogExcecutionContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public CatalogEntry readVanityId(String vanityId, CatalogDescriptor catalog, CatalogExcecutionContext context)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public CatalogEntry update(CatalogEntry originalEntry, CatalogEntry updatedEntry, CatalogExcecutionContext context)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public CatalogEntry delete(Object targetEntryId, CatalogExcecutionContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected <T extends CatalogEntry> T trash(CatalogDescriptor catalog, Class<T> clazz, Object targetEntryId, FieldDescriptor trashField,
			CatalogExcecutionContext context) throws Exception {
		CatalogDataAccessObject<T> dao = getOrAssembleDataSource(catalog, context, clazz);
		T originalEntry = dao.read(targetEntryId);
		Session session = accessor.newSession(originalEntry);
		accessor.setPropertyValue(catalog, trashField, originalEntry, true, session);
		originalEntry = dao.update(originalEntry, originalEntry);
		// log.trace("[TRASH CATALOG ENTRY should be triggered by now]");
		return originalEntry;
	}

	protected void undelete(Trash e, CatalogExcecutionContext context) throws Exception {
		String catalogId = e.getCatalogId();
		CatalogDescriptor descriptor = database.getDescriptorForName(catalogId, context);
		CatalogDataAccessObject<CatalogEntry> dao = getOrAssembleDataSource(descriptor, context, CatalogEntry.class);
		FieldDescriptor trashField = descriptor.getFieldDescriptor(Trash.TRASH_FIELD);
		Session session = accessor.newSession(null);
		CatalogDataAccessObject<Trash> trashDao = getOrAssembleDataSource(database.getDescriptorForName(Trash.CATALOG, context), context, Trash.class);

		undelete(e, context, descriptor, trashField, session, trashDao, dao);
	}

	protected void undelete(Trash e, CatalogExcecutionContext context, CatalogDescriptor descriptor, FieldDescriptor trashField, Session session,
			CatalogDataAccessObject<Trash> trashDao, CatalogDataAccessObject<CatalogEntry> dao) throws Exception {
		if (e.isRestored()) {
			log.trace("[UNDELETE] {}", e);
			Object entryId = e.getCatalogEntryId();
			CatalogEntry trashedEntry = dao.read(entryId);
			accessor.setPropertyValue(descriptor, trashField, trashedEntry, false, session);
			dao.update(trashedEntry, trashedEntry);
			// DUMP TRASH
			trashDao.delete(e);
		}
	}

	protected <T extends CatalogEntry> CatalogDataAccessObject<T> getOrAssembleDataSource(CatalogDescriptor catalog, CatalogExcecutionContext context,
			Class<T> clazzz) throws Exception {

		String cacheKey = new StringBuilder(CONTEXT_PREFIX_DAO.length() + catalog.getCatalogId().length()).append(CONTEXT_PREFIX_DAO)
				.append(catalog.getCatalogId()).toString();
		CatalogDataAccessObject<T> regreso = (CatalogDataAccessObject<T>) context.getDomainContext().get(cacheKey);

		if (regreso == null) {
			log.trace("[ASSEMBLE DATA SOURCE]", catalog.getCatalogId());
			Class<? extends CatalogEntry> clazz = catalog.getJavaClass();
			CatalogDataAccessObject<? extends CatalogEntry> wrapped = database.getOrAssembleDataSource(catalog, clazz, context);
			// unless indicated catalogs use per-query cache on the server-side,
			// clients may implement diferent catalog cache policies
			log.trace("[ASSEMBLED] {}", wrapped);
			regreso = new CatalogManagedDaoImpl<T>(queryRewriter, catalog.getOptimization() != 2 /* NO_CACHE */ ? cacheProvider.get() : null,
					trigererProvider.get(), accessor, wrapped, catalog);
			context.getDomainContext().put(cacheKey, regreso);
		} else {
			log.trace("[REUSE DATA SOURCE]");
		}
		regreso.setContext(context);
		return regreso;
	}

	protected <T extends CatalogEntry> CatalogDataAccessObject<T> unwrap(CatalogDataAccessObject<T> dao) {
		return ((CatalogManagedDaoImpl<T>) dao).wrapped;
	}

	private final static class CatalogManagedDaoImpl<T extends CatalogEntry> implements CatalogDataAccessObject<T> {
		private final CatalogDataAccessObject<T> wrapped;
		private final CatalogActionTriggerHandler trigerer;
		private final CatalogDescriptor catalog;
		private final CatalogPropertyAccesor accessor;
		private final CatalogResultCache cache;
		private final CatalogQueryRewriter queryRewriter;
		private CatalogExcecutionContext context;

		private CatalogManagedDaoImpl(CatalogQueryRewriter queryRewriter, CatalogResultCache cache, CatalogActionTriggerHandler trigerer,
				CatalogPropertyAccesor accessor, CatalogDataAccessObject wrapped, CatalogDescriptor descriptor) {
			super();
			this.catalog = (CatalogDescriptor) descriptor;
			this.cache = cache;
			this.trigerer = trigerer;
			if (cache == null) {
				log.debug("[CATALOG DAO] NO CACHE");
			}
			List<CatalogActionTrigger> trigerrs = catalog.getTriggersValues();
			if (trigerrs != null) {
				for (CatalogActionTrigger trigger : trigerrs) {
					trigerer.process(trigger);
				}

			}
			this.queryRewriter = queryRewriter;
			this.accessor = accessor;
			this.wrapped = wrapped;

		}

		@Override
		public List<T> read(FilterData filterData) throws Exception {
			List<T> regreso;
			filterData = queryRewriter.rewriteFilter(filterData, context, catalog);
			if (cache == null) {

				regreso = wrapped.read(filterData);
			} else {
				regreso = cache.satisfy(filterData);
				if (regreso == null) {
					regreso = wrapped.read(filterData);
					if (regreso != null) {
						cache.put(regreso, filterData);
					}
				}
			}

			((CatalogUserTransaction) context.getRequest().getTransaction(context)).didRead(catalog, regreso, wrapped);
			return regreso;
		}

		@Override
		public T read(Object targetEntryId) throws Exception {
			T regreso;
			if (cache == null) {
				regreso = (T) wrapped.read(targetEntryId);
			} else {
				regreso = cache.get(targetEntryId);
				if (regreso == null) {
					regreso = (T) wrapped.read(targetEntryId);
					if (regreso != null) {
						cache.put(regreso);
					}
				}
			}

			((CatalogUserTransaction) context.getRequest().getTransaction(context)).didRead(catalog, regreso, wrapped);
			queryRewriter.maybeBlockEntry(regreso, context, catalog);
			return regreso;
		}

		@Override
		public T update(T outDatedEntry, T updatedEntry) throws Exception {
			T originalEntryCopy = (T) accessor.catalogCopy(catalog, outDatedEntry);
			performBeforeUpdate(outDatedEntry, updatedEntry);
			T regreso = (T) wrapped.update(outDatedEntry, updatedEntry);
			((CatalogUserTransaction) context.getRequest().getTransaction(context)).didUpdate(catalog, originalEntryCopy, outDatedEntry, wrapped);
			performAfterUpdate(regreso);
			if (cache != null) {
				cache.update(outDatedEntry, regreso);
			}
			addBroadcastable(new CatalogBroadcastDataImpl(context.getDomain(), catalog.getCatalogId(), CatalogActionRequest.WRITE_ACTION, regreso), context);
			return regreso;
		}

		@Override
		public T create(T o) throws Exception {
			checkBedoreCreate(o);
			T regreso = (T) wrapped.create(o);
			((CatalogUserTransaction) context.getRequest().getTransaction(context)).didCreate(catalog, regreso, wrapped);
			checkAfterCreate(regreso);
			if (cache != null) {
				cache.create(o);
			}
			addBroadcastable(new CatalogBroadcastDataImpl(context.getDomain(), catalog.getCatalogId(), CatalogActionRequest.CREATE_ACTION, o), context);
			return regreso;
		}

		@Override
		public T delete(T o) throws Exception {
			performBeforeDelete(o);
			T regreso = (T) wrapped.delete(o);
			((CatalogUserTransaction) context.getRequest().getTransaction(context)).didDelete(catalog, regreso, wrapped);
			performAfterDelete(regreso);
			if (cache != null) {
				cache.delete(o);
			}
			addBroadcastable(new CatalogBroadcastDataImpl(context.getDomain(), catalog.getCatalogId(), CatalogActionRequest.DELETE_ACTION, regreso), context);
			return regreso;
		}

		private void performAfterUpdate(T regreso) throws Exception {
			if (trigerer != null) {
				Trigger thriger = trigerer.getUpdateHandler();
				if (thriger != null) {
					thriger.after(catalog, regreso, context);
				}
			}
		}

		private void performBeforeUpdate(T originalEntry, T updatedEntry) throws Exception {
			if (trigerer != null) {
				Trigger thriger = trigerer.getUpdateHandler();
				if (thriger != null) {
					thriger.before(catalog, originalEntry, updatedEntry, context);
				}
			}
		}

		private void checkAfterCreate(T regreso) throws Exception {
			if (trigerer != null) {
				Trigger thriger = trigerer.getCreateHandler();
				if (thriger != null) {
					thriger.after(catalog, regreso, context);
				}
			}
		}

		private void checkBedoreCreate(T o) throws Exception {
			if (trigerer != null) {
				Trigger thriger = trigerer.getCreateHandler();
				if (thriger != null) {
					thriger.before(catalog, o, o, context);
				}
			}
		}

		private void performAfterDelete(T regreso) throws Exception {
			if (trigerer != null) {
				Trigger thriger = trigerer.getDeleteHandler();
				if (thriger != null) {
					thriger.after(catalog, regreso, context);
				}
			}
		}

		private void performBeforeDelete(T o) throws Exception {
			if (trigerer != null) {
				Trigger thriger = trigerer.getDeleteHandler();
				if (thriger != null) {
					thriger.before(catalog, o, o, context);
				}
			}
		}

		@Override
		public void setContext(CatalogExcecutionContext context) {
			if (cache != null) {
				cache.init(context, catalog.getCatalogId());
			}

			this.context = context;
			wrapped.setContext(context);
		}

		@Override
		public void beginTransaction() throws NotSupportedException, SystemException {
			wrapped.beginTransaction();
		}

		@Override
		public void commitTransaction()
				throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
			wrapped.commitTransaction();
		}

		@Override
		public void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
			wrapped.rollbackTransaction();
		}

		@Override
		public CatalogExcecutionContext getContext() {
			return context;
		}

		private static void addBroadcastable(CatalogBroadcastData data, CatalogExcecutionContext ctx) {
			if (data != null) {
				if (data.getEntry() != null) {
					ctx = ctx.getRootAncestor();
					List<CatalogBroadcastData> list = (List<CatalogBroadcastData>) ctx.get(CatalogBroadcastData.class.getSimpleName());
					if (list == null) {
						list = new ArrayList<CatalogBroadcastData>(2);
						ctx.put(CatalogBroadcastData.class.getName(), list);
					}
					log.trace("[stored catalog broadcast event to dispatch later...]");
					list.add(data);
				}
			}
		}

	}

	

}
