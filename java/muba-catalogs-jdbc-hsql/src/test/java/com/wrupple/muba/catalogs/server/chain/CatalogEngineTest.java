package com.wrupple.muba.catalogs.server.chain;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.IntegralTest;
import com.wrupple.muba.catalogs.domain.Argument;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.MathProblem;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.impl.FilterCriteriaImpl;
import com.wrupple.muba.event.domain.impl.FilterDataOrderingImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CatalogEngineTest extends IntegralTest {


	/**
	 * <ol>
	 * <li>create math problem catalog (with getInheritance)</li>
	 * <li></li>
	 * <li></li>
	 * </ol>
	 * 
	 * @throws Exception
	 */
	@Test
	public void engineTest() throws Exception {

		CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
		log.info("[-create catalog-]");

		// expectations

		replayAll();

		CatalogDescriptor problemContract = builder.fromClass(MathProblem.class, MathProblem.class.getSimpleName(),
				"Math Problem",  injector.getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));
        problemContract.setConsolidated(false);
		FieldDescriptor solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue( solutionFieldDescriptor!= null);
        assertTrue("does metadata describe problem as inherited?",problemContract.getParent()!=null);

        CatalogDescriptor argumentContract = builder.fromClass(
                Argument.class,
                Argument.class.getSimpleName(),
                "Argument",
                null);

		CatalogActionRequestImpl action = new CatalogActionRequestImpl();
		action.setEntryValue(argumentContract);
        action.setFollowReferences(true);
		runtimeContext.setServiceContract(action);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
				CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
		// locale is set in catalog
		runtimeContext.process();
        runtimeContext.reset();

        action = new CatalogActionRequestImpl();
        action.setEntryValue(problemContract);
        action.setFollowReferences(true);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        runtimeContext.setServiceContract(action);
        runtimeContext.process();

        CatalogActionContext catalogContext = runtimeContext.getServiceContext();

		problemContract = catalogContext.getEntryResult();
		assertTrue(problemContract.getId() != null);
        assertTrue(problemContract.getDomain() != null);
		assertTrue(problemContract.getDomain().longValue() == CatalogEntry.PUBLIC_ID);
		assertTrue("does metadata describe problem as inherited?",problemContract.getParent()!=null);
        assertTrue("does metadata provide problem's parent type?",problemContract.getParentValue()!=null);

        assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));
		solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
		assertTrue( solutionFieldDescriptor!= null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues()!=null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues().size()==2);

		log.info("[-see changes in catalog list-]");

		runtimeContext.reset();

		runtimeContext.setServiceContract(null);
		runtimeContext.setSentence(
		        CatalogServiceManifest.SERVICE_NAME,
                CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD,
                CatalogDescriptor.CATALOG_ID,
				CatalogActionRequest.READ_ACTION);
		runtimeContext.process();
		catalogContext = runtimeContext.getServiceContext();
		List<CatalogEntry> catalogList = catalogContext.getResults();
		assertTrue(catalogList != null);
		assertTrue(!catalogList.isEmpty());
		boolean contained = false;
        log.info("Looking for just created catalog {}={}",problemContract.getId(),problemContract.getName());
		for (CatalogEntry existingCatalog : catalogList) {
		    log.info("Existing catalog {}={}",existingCatalog.getId(),existingCatalog.getName());
			if(existingCatalog.getId().equals(problemContract.getId())){
				contained = true ;
				break;
			}
		}

		assertTrue(contained);
		log.info("[-see registered catalog Descriptor-]");
		runtimeContext.reset();
		action = new CatalogActionRequestImpl();
		action.setFollowReferences(true);
        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(
                CatalogServiceManifest.SERVICE_NAME,
                CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD,
                CatalogDescriptor.CATALOG_ID,
                CatalogActionRequest.READ_ACTION,
                MathProblem.class.getSimpleName());
        runtimeContext.process();
        catalogContext = runtimeContext.getServiceContext();

        problemContract = catalogContext.getConvertedResult();
        log.info("[-verifying catalog graph integrity-]");
        assertTrue(problemContract.getId() != null);
        assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));
        solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue(solutionFieldDescriptor!= null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues()!=null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues().size()==2);
        assertTrue("does metadata describe problem as inherited?",problemContract.getParentValue()!=null);
		assertTrue("does metadata include it's ancestry?",problemContract.getRootAncestor()!=null);
		assertTrue("does metadata describe problem as a timeline?",ContentNode.CATALOG_TIMELINE.equals(problemContract.getRootAncestor().getDistinguishedName()));



		log.debug("-create math problem entry-");
		runtimeContext.reset();
		MathProblem problem = new MathProblem();
		problem.setName(MathProblem.class.getSimpleName());
		problem.setSolution(4l);
		Argument argument = new Argument();
		argument.setProblemValue(problem);
        problem.setArgumentsValues(Arrays.asList(argument));

		CatalogActionRequest contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID,
				problemContract.getDistinguishedName(), CatalogActionRequest.CREATE_ACTION, null, null, problem, null);
		contract.setFollowReferences(true);
		runtimeContext.setServiceContract(contract);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
				CatalogActionRequest.LOCALE_FIELD, MathProblem.class.getSimpleName(),
				CatalogActionRequest.CREATE_ACTION);
		runtimeContext.process();

		problem = ((CatalogActionContext) runtimeContext.getServiceContext()).getEntryResult();
		assertTrue(problem.getId() != null);
		assertTrue(problem.getSolution() != null);
		assertTrue("Is Timestamper trigger called?",problem.getTimestamp() != null);
		assertTrue("are foreign keys registered",	problem.getArguments()!=null);
		assertTrue("data graph is incomplete",	problem.getArgumentsValues()!=null);
		argument = problem.getArgumentsValues().get(0);
		assertTrue("circular data dependency not resolved",argument.getProblemValue()!=null);
		assertTrue("Is circular data dependency identity lost",argument.getProblemValue().getId().equals(problem.getId()));

		log.debug("-check if problem was created-");
		runtimeContext.reset();

		contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID, ContentNode.CATALOG_TIMELINE,
				CatalogActionRequest.READ_ACTION, null, null, null, FilterDataUtils.newFilterData());
		runtimeContext.setServiceContract(contract);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
				CatalogActionRequest.LOCALE_FIELD, ContentNode.CATALOG_TIMELINE, CatalogActionRequest.READ_ACTION);

		runtimeContext.process();

		catalogContext = runtimeContext.getServiceContext();

		assertTrue(catalogContext.getResults() != null);
		assertTrue(catalogContext.getResults().size() == 1);
		assertTrue(catalogContext.getResults().get(0).getName().equals(problem.getName()));

	}


	//@Test
	public void filters() throws Exception {

		RuntimeContext excecution = injector.getInstance(RuntimeContext.class);
		SystemCatalogPlugin manager = injector.getInstance(SystemCatalogPlugin.class);
		CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
		CatalogDescriptor catalog = builder.fromClass(Argument.class, "Argument", "Argument", -49723l, null);

		CatalogActionRequestImpl request = new CatalogActionRequestImpl();
		request.setCatalog(CatalogDescriptor.CATALOG_ID);
		request.setDomain(CatalogEntry.PUBLIC_ID);
        request.setName(DataContract.CREATE_ACTION);
		log.info("NEW TEST EXCECUTION CONTEXT READY");

		String TRES = "TRES";
		String FIVE = "five";

		log.info("[-create elements-]");

		List<Argument> argumentsToDeclare = Arrays.asList(
				new Argument(TRES, 3l),
				new Argument(FIVE, 5l),
				new Argument("one", 1l),
				new Argument("uno", 1l),
				new Argument("four", 4l)
		);

		for (Argument arg : argumentsToDeclare) {
			request.setEntryValue(arg);
			runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		}
		log.info("[-read all-]");

		setUp();
		FilterData filterData = FilterDataUtils.newFilterData();
		request.setFilter(filterData);

        List<CatalogEntry> results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

		assertTrue(results.size() == argumentsToDeclare.size());


		log.info("[-read element-]");
		setUp();
		Object lodId = results.get(0).getId();
		request.setEntry(lodId);
        request.setName(DataContract.READ_ACTION);
		results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getName().equals(TRES));

		log.info("[-read all in order-]");
		// ORDER
		request.setEntry(null);
		request.setFilter(filterData);
		filterData.addOrdering(new FilterDataOrderingImpl(Argument.VALUE, false));

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

		assertTrue(results.size() == argumentsToDeclare.size());
		assertTrue(((Argument) results.get(0)).getValue() == 5);

		log.info("[-read a segment-]");
		// LIMITS log.info("[-read a segment-]");
		filterData.setConstrained(true);
		filterData.setStart(2);
		filterData.setLength(2);
        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		assertTrue(results.size() == 2);

		// EQUALS = "==";
		log.info("[-find element by single == criteria-]");
		filterData = FilterDataUtils.createSingleFieldFilter(CatalogEntry.NAME_FIELD, TRES);
		request.setFilter(filterData);


		results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getName().equals(TRES));

		filterData = FilterDataUtils.newFilterData();
		FilterCriteriaImpl criteria = new FilterCriteriaImpl(Argument.VALUE, FilterData.DIFFERENT, 1l);
		filterData.addFilter(criteria);
		request.setFilter(filterData);

		// DIFFERENT
		log.info("[-find element by single != criteria-]");
		criteria.setOperator(FilterData.DIFFERENT);


		results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		assertTrue(results.size() == 3);

		// GREATEREQUALS = ">=";
		log.info("[-find element by single >= criteria-]");
		criteria.setOperator(FilterData.GREATEREQUALS);

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 5);

		// LESSEQUALS = "<=";
		log.info("[-find element by single <= criteria-]");
		criteria.setOperator(FilterData.LESSEQUALS);

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 2);

		// LESS = "<"
		log.info("[-find element by single < criteria-]");
		criteria.setOperator(FilterData.LESS);
		criteria.setValue(2l);

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 2);

		// GREATER = ">"
		log.info("[-find element by single > criteria-]");
		criteria.setOperator(FilterData.GREATER);

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 3);

		log.debug("testing string filters");
		// LIKE
		log.info("[-find element by single LIKE criteria-]");
		criteria.setPath(Arrays.asList(CatalogEntry.NAME_FIELD));
		criteria.setOperator(FilterData.LIKE);

		criteria.setValue("f???");

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 1);

		criteria.setValue("f????");

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 0);

		criteria.setValue("f%");

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 2);




		log.info("[-find element by single STARTS criteria-]");

		criteria.setOperator(FilterData.STARTS); criteria.setValue("f");

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 2);



		log.info("[-find element by single ENDS criteria-]");
		criteria.setOperator(FilterData.ENDS); criteria.setValue("o");

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 1);



		log.info("[-find element by single REGEX criteria-]");
		criteria.setOperator(FilterData.REGEX);
		criteria.setValue("([A-Z])\\w+");

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 1);


		log.debug("testing collection filters");
		// IN
		log.info("[-find element by single IN criteria-]");
		criteria.setOperator(FilterData.IN);
		criteria.setValues(Arrays.asList((Object)TRES,FIVE));

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 2);

		/*
		 * FINISHED QUERY TESTS
		 */
		log.info("[update element]");
		//request.setOldValue((CatalogEntry) request.getConvertedResult());
		request.setEntry(lodId);
		request.setEntryValue(new Argument("TROI", 3l));
        request.setName(DataContract.WRITE_ACTION);

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		assertTrue(results.size() == 1);
		assertTrue(lodId.equals(results.get(0).getId()));
		assertTrue(results.get(0).getName().equals("TROI"));

		log.info("[delete element]");
		setUp();
		request.setEntry(lodId);
        request.setName(DataContract.DELETE_ACTION);

        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == 1);
		assertTrue(lodId.equals(results.get(0).getId()));

		setUp();

		filterData = FilterDataUtils.newFilterData();
		request.setFilter(filterData);
        request.setEntry(null);
        request.setName(DataContract.READ_ACTION);
        results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

        assertTrue(results.size() == (argumentsToDeclare.size() - 1));

		log.info("[CRUD tests passed]");

	}


	// i18n locale-dependent field values
	// DROP COLUMN/INDEX CatalogDescriptorUpdateTriggerImpl

	// ADD COLUMN/INDEX
	// pluggable! FIXME delete all catalogs of a domain when domain is dropped
	// FIXME Clean entities with no corresponding catalog in namespace
	// https://cloud.google.com/appengine/docs/java/datastore/metadataqueries?csw=1#Namespace_Queries
	/*
	 * @Test is multiple delete handles by triggers gracefully (batch processes
	 * in general) i18n LocalizedEntityInterceptor security for unanted
	 * crossdomain access by querying for ids trash, restore, dump public
	 * timeline
	 * 
	 * transactiondemarcation() {
	 * 
	 * @Test public void vanityId() { fail("Not yet implemented"); }
	 * 
	 * @Test public void distributedLocalization() { fail("Not yet implemented"
	 * ); }
	 * 
	 * @Test public void centralizedLocalization() { fail("Not yet implemented"
	 * ); }
	 * 
	 * 
	 * @Test public void hardKeys() { fail("Not yet implemented"); }
	 */

}
