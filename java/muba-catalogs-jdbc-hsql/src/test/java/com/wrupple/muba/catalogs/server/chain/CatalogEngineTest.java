package com.wrupple.muba.catalogs.server.chain;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.wrupple.muba.IntegralTest;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.junit.Before;
import org.junit.Test;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;

public class CatalogEngineTest extends IntegralTest {


	/**
	 * <ol>
	 * <li>create math problem catalog (with inheritance)</li>
	 * <li></li>
	 * <li></li>
	 * </ol>
	 * 
	 * @throws Exception
	 */
	@Test
	public void engineTest() throws Exception {

		CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
		log.trace("[-create catalog-]");

		// expectations

		replayAll();

		CatalogDescriptor problemContract = builder.fromClass(MathProblem.class, MathProblem.class.getSimpleName(),
				"Math Problem", 0, builder.fromClass(ContentNodeImpl.class, ContentNode.CATALOG,
						ContentNode.class.getSimpleName(), -1l, null));
        FieldDescriptor solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue( solutionFieldDescriptor!= null);

		CatalogActionRequestImpl action = new CatalogActionRequestImpl();
		action.setEntryValue(problemContract);
        //action.setFollowReferences(true);
		runtimeContext.setServiceContract(action);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
		// locale is set in catalog
		runtimeContext.process();

		CatalogActionContext catalogContext = runtimeContext.getServiceContext();

		problemContract = catalogContext.getEntryResult();
		assertTrue(problemContract.getId() != null);
		assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));
		solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
		assertTrue( solutionFieldDescriptor!= null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues()!=null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues().size()==2);

		log.trace("[-see changes in catalog list-]");

		runtimeContext.reset();

		runtimeContext.setServiceContract(null);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogActionRequest.READ_ACTION);
		runtimeContext.process();
		catalogContext = runtimeContext.getServiceContext();
		assertTrue(catalogContext.getResults() != null);
		assertTrue(!catalogContext.getResults().isEmpty());
		boolean contained = false;

		for (CatalogEntry id : catalogContext.getResults()) {
			if(id.getId().equals(problemContract.getDistinguishedName())){
				contained = true ;
				break;
			}
		}

		assertTrue(contained);
		log.trace("[-see registered catalog Descriptor-]");
		runtimeContext.reset();

        catalogContext.setCatalog(MathProblem.class.getSimpleName());
        problemContract = catalogContext.getCatalogDescriptor();
        log.trace("[-verifying catalog graph integrity-]");
        assertTrue(problemContract.getId() != null);
        assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));
        solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue(solutionFieldDescriptor!= null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues()!=null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues().size()==2);


		log.debug("-create math problem entry-");
		runtimeContext.reset();
		MathProblem problem = new MathProblem();
		problem.setName(MathProblem.class.getSimpleName());
		CatalogActionRequest contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID,
				problemContract.getDistinguishedName(), CatalogActionRequest.CREATE_ACTION, null, null, problem, null);
		runtimeContext.setServiceContract(contract);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, MathProblem.class.getSimpleName(),
				CatalogActionRequest.CREATE_ACTION);

		runtimeContext.process();

		problem = ((CatalogActionContext) runtimeContext.getServiceContext()).getEntryResult();
		assertTrue(problem.getId() != null);
		assertTrue(problem.getTimestamp() != null);

		log.debug("-check if child was created-");
		runtimeContext.reset();

		contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID, ContentNode.CATALOG,
				CatalogActionRequest.READ_ACTION, null, null, null, FilterDataUtils.newFilterData());
		runtimeContext.setServiceContract(contract);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, ContentNode.CATALOG, CatalogActionRequest.READ_ACTION);

		runtimeContext.process();

		catalogContext = runtimeContext.getServiceContext();

		assertTrue(catalogContext.getResults() != null);
		assertTrue(catalogContext.getResults().size() == 1);
		assertTrue(catalogContext.getResults().get(0).getName().equals(problem.getName()));

	}
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
