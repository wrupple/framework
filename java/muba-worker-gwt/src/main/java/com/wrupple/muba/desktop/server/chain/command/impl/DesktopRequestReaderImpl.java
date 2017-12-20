package com.wrupple.muba.desktop.server.chain.command.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.server.service.*;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.desktop.server.chain.command.DesktopRequestReader;
import com.wrupple.muba.desktop.server.domain.DesktopBuilderContext;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.services.SessionContext;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class DesktopRequestReaderImpl implements DesktopRequestReader {

	private String desktopTitle;
	private String characterEncoding;
	private String mubaCSS;
	private String wruppleJs;
	private String raphaelJs;
	private String channelApi;

	private final SessionContext sessionDataService;

	private final DesktopSliceWriter[] sliceWriters;
	private final DataStoreManager daoFactory;
	private String defaultActivity;

	@Inject
	public DesktopRequestReaderImpl(DataStoreManager daoFactory, WruppleServerModuleRegistry registry, CatalogEntryBeanDAO dao, SessionContext sessionData,
			SessionContext sessionDataService, @Named("desktop.defaultActivity") String defaultActivity, @Named("Desktop Title") String desktopTitle,
			@Named("Charset") String characterEncoding, @Named("muba css") String mubaCSS, @Named("gae channel api") String channelApi,
			@Named("wrupple code") String wruppleJs, @Named("raphael code") String raphaelJs) {
		super();
		this.daoFactory = daoFactory;
		this.channelApi = channelApi;
		this.sliceWriters = registry.getModules();
		this.desktopTitle = desktopTitle;
		this.characterEncoding = characterEncoding;
		this.mubaCSS = mubaCSS;
		this.wruppleJs = wruppleJs;
		this.raphaelJs = raphaelJs;
		this.defaultActivity = defaultActivity;
		this.sessionDataService = sessionDataService;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		DesktopBuilderContext context = (DesktopBuilderContext) c;
        String submittingTaskId = context.getRequest().getParameter(DesktopRequestReader.SUBMITTING_TASK);

        if(submittingTaskId!=null){
			CatalogDataAccessObject<ProcessTaskDescriptor> taskDao = context.getDataStoreManager().getOrAssembleDataSource(ProcessTaskDescriptor.CATALOG, context.getCatalogContext(), ProcessTaskDescriptor.class);
			ProcessTaskDescriptor submittingTask = taskDao.read(submittingTaskId);
			String transactionType = submittingTask.getTransactionType();
			/*
			 * all forms that use a context.getSubmitUrl() should append a hidden field with the id of the submitting task.  if this field is present we know we have a form submission in our hands
			 * 
			 * in that case the use output context variable is constituted by the resulting entrie(s) of committing the submitting task
			 * 
			 * see how to handle different submissions cause redirecting is a posibility, se how submitUrl is determined to also know how to handle sumissions
			 *
			
			also userOutputVariable should be used, since client uses that variable to deduce what item to show in read transactions
			El cliente debería de poder determinar el estado en el que se encuentra una petición únicamente por la URL
			
			reader should check for submitts before wasting any more resources and perform 
			redirects filling out apropiate url tokens (at least entry and ?catalog? according to task configuration if necesary
			*/
			
			if(CatalogActionRequest.READ_ACTION.equals(transactionType)){
				//assume a selection is submitted
				String[] selectedIds  = context.getRequest().getParameterValues(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);
				
				//TODO what to do now that i've selected? just save them to user output
				
			}else if(CatalogActionRequest.WRITE_ACTION.equals(transactionType)){
				
				String updatedId  = context.getRequest().getParameter(CatalogEntry.ID_FIELD);
				
				// TODO write y guardarlo en user output

				
			}else if(CatalogActionRequest.CREATE_ACTION.equals(transactionType)){
			
				//TODO create and save to user output
				
			}else{
				throw new IllegalArgumentException("unsupported ");
			}
			
					
					
		}
		
		//set default desktop title (can and should be changed later on)
		context.setDesktopTitle(desktopTitle);
		context.setCharacterEncoding(characterEncoding);

		String rootActivity =context.getHomeActivity();
		if (rootActivity == null || rootActivity.isEmpty()) {
			rootActivity = defaultActivity;
		}
		context.setHomeActivity(rootActivity);

		ApplicationItem domainRoot ;
		try{
			domainRoot= buildCurrentDomainSlice(context,rootActivity);
		}catch (IndexOutOfBoundsException e){
			// Most likely because domain has not been set up
			domainRoot = null;
			context.setSetupFlag(true);
		}
		
		if (domainRoot!=null && sliceWriters != null) {
			for (DesktopSliceWriter sliceWriter : sliceWriters) {
				sliceWriter.writeItems(domainRoot);
			}
		}
		context.setDesktopPlaceHierarchy(domainRoot);

		System.err.println("will now read styles");
		context.setStaticDesktopCssURI(new String[] { this.mubaCSS });
		context.setStaticDesktopJavaScriptURI(new String[] { this.raphaelJs, this.wruppleJs, this.channelApi });


		return CONTINUE_PROCESSING;
	}

	private ApplicationItem buildCurrentDomainSlice(DesktopBuilderContext context, String homeActivity) throws Exception {
		CatalogDataAccessObject<ApplicationItem> dsm = daoFactory.getOrAssembleDataSource(ApplicationItem.CATALOG, context.getCatalogContext(),
				ApplicationItem.class);
		ApplicationItem domainRoot;
		FilterData filter = FilterDataUtils.createSingleFieldFilter("activity", homeActivity);
		List<ApplicationItem> domainRoots = dsm.read(filter);
		domainRoot = domainRoots.get(0);
		if (domainRoot == null) {
			throw new IllegalArgumentException("no domain root");
		}
		domainRoot = buildItemTree(domainRoot, dsm);

		return domainRoot;
	}

	private ApplicationItem buildItemTree(ApplicationItem item, CatalogDataAccessObject<ApplicationItem> dsm) {
		List<Long> childItems = item.getChildItems();
		String requiredRole;
		
		boolean rolIsGranted;
		
		ApplicationItem child;

		if (childItems != null && !childItems.isEmpty()) {

			List<ApplicationItem> childInstances = new ArrayList<ApplicationItem>(childItems.size());

			for (Long childId : childItems) {
				try {
					child = dsm.read(String.valueOf(childId));
				} catch (Exception e) {
					child = null;
				}
				if (child != null) {
					requiredRole = child.getRequiredRole();
					if (requiredRole == null) {
						childInstances.add(child);
					} else {
						rolIsGranted = sessionDataService.isGranted(requiredRole);
						if (rolIsGranted) {
							childInstances.add(child);
						} else {
							System.out.println("CHILD ITEM SKIPPED " + childId);
						}
					}
				} else {
					// TODO generate a notification
					System.err.println("MISSING ITEM " + childId);
				}

			}

			item.setChildItemsValues(childInstances);

			for (ApplicationItem grandChild : childInstances) {
				buildItemTree(grandChild, dsm);
			}

		}
		return item;
	}

}
