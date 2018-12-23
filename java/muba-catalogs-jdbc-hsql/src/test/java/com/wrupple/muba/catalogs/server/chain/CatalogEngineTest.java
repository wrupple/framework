package com.wrupple.muba.catalogs.server.chain;

import com.wrupple.muba.IntegralTest;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.event.domain.impl.*;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CatalogEngineTest extends IntegralTest {


	@Test
	public void circularDependency() throws Exception {

		log.debug("-many to one circular dependency-");

		Credit credit = new Credit();
		credit.setName("new credit");
		Endorser endorser = new Endorser();
		credit.setEndorserValue(endorser);
		endorser.setCreditsValues(Collections.singletonList(credit));

		CatalogActionRequest contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID, Credit.CATALOG, CatalogActionRequest.CREATE_ACTION, null, null, credit, null);
		contract.setFollowReferences(true);
		runtimeContext.setServiceContract(contract);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,CatalogActionRequest.LOCALE_FIELD, Credit.CATALOG, CatalogActionRequest.CREATE_ACTION);
		runtimeContext.process();

		credit = ((CatalogActionContext) runtimeContext.getServiceContext()).getEntryResult();
		assertTrue(credit.getId() != null);
		assertTrue(credit.getEndorserValue() != null);
		assertTrue("memory identity is not preserved",credit.getEndorserValue()==endorser);
		assertTrue("circular data dependency not resolved",endorser.getCreditsValues().get(0)!=null);
		assertTrue("circular data dependency lost",endorser.getCreditsValues().get(0).getId().equals(credit.getId()));

	}


	@Test
	public void engineTest() throws Exception {

		log.debug("-create math problem entry-");
		runtimeContext.reset();
		MathProblem problem = new MathProblem();
		problem.setName(MathProblem.class.getSimpleName());
		problem.setSolution(4l);
		Argument argument = new Argument("uno",1L);
        Argument secondArg = new Argument("dos",2L);
        argument.setProblemValue(problem);
		problem.setArgumentsValues(Arrays.asList(argument,secondArg));

		CatalogActionRequestImpl contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID,
				problemContract.getDistinguishedName(), CatalogActionRequest.CREATE_ACTION, null, null, problem, null);
		contract.setFollowReferences(true);
		runtimeContext.setServiceContract(contract);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
				CatalogActionRequest.LOCALE_FIELD, MathProblem.class.getSimpleName(),
				CatalogActionRequest.CREATE_ACTION);
		runtimeContext.process();

		((CatalogActionContext) runtimeContext.getServiceContext()).getEntryResult();
		assertTrue(problem.getId() != null);
		assertTrue(problem.getSolution() != null);
		assertTrue("Is Timestamper trigger called?",problem.getTimestamp() != null);
		assertTrue("are foreign keys registered",	problem.getArguments()!=null);
		assertTrue("data graph is incomplete",	problem.getArgumentsValues()!=null);
		assertTrue("data graph is incomplete",problem.getArgumentsValues().size()==2);
        assertTrue("data graph is inconsistent",problem.getArgumentsValues().get(0).getValue().equals(1L));
        assertTrue("data graph is inconsistent",problem.getArgumentsValues().get(1).getValue().equals(2L));
        argument = problem.getArgumentsValues().get(0);

		assertTrue("circular data dependency not created",argument.getId()!=null);
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

		CatalogActionContext catalogContext = runtimeContext.getServiceContext();

		assertTrue(catalogContext.getResults() != null);
		assertTrue(catalogContext.getResults().size() == 1);
		assertTrue(catalogContext.getResults().get(0).getName().equals(problem.getName()));

	}


	@Test
	public void filters() throws Exception {

		String argumentCatalog = Argument.CATALOG;
		CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
		CatalogDescriptor catalog = builder.fromClass(Argument.class, argumentCatalog, argumentCatalog, -49723l, null);

		CatalogActionRequestImpl request = new CatalogCreateRequestImpl(catalog,CatalogDescriptor.CATALOG_ID);
		runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
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
			request = new CatalogCreateRequestImpl(arg,argumentCatalog);
			runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		}
		log.info("[-read all-]");

		FilterData filterData = FilterDataUtils.newFilterData();
		request = new CatalogQueryRequestImpl(filterData,argumentCatalog);

        List<CatalogEntry> results = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);

		assertTrue(results.size() == argumentsToDeclare.size());


		log.info("[-read element-]");
		Object lodId = results.get(0).getId();
		request = new CatalogReadRequestImpl(lodId,argumentCatalog);
		Argument result = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		assertTrue(result!=null);
		assertTrue(result.getName().equals(TRES));

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

        /*
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

		*
		 * FINISHED QUERY TESTS
		 */
		log.info("[update element]");
		request.setEntry(lodId);
		request.setEntryValue(new Argument("TROI", 3l));
        request.setName(DataContract.WRITE_ACTION);

        result = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);
		assertTrue(result!=null);
		assertTrue(lodId.equals(result.getId()));
		assertTrue(result.getName().equals("TROI"));

		log.info("[delete element]");
		request.setEntry(lodId);
        request.setName(DataContract.DELETE_ACTION);

		result = runtimeContext.getServiceBus().fireEvent(request,runtimeContext,null);


		assertTrue(result!=null);
		assertTrue(lodId.equals(result.getId()));


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
