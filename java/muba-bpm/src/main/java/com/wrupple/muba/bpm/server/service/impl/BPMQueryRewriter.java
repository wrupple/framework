package com.wrupple.muba.bpm.server.service.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;
import com.wrupple.vegetate.domain.HasStakeHolder;
import com.wrupple.vegetate.domain.Person;

@Singleton
public class BPMQueryRewriter implements CatalogQueryRewriter {
	private static final Logger log = LoggerFactory.getLogger(BPMQueryRewriter.class);
	private final CatalogPropertyAccesor accesor;
	
	@Inject
	public BPMQueryRewriter(CatalogPropertyAccesor accesor) {
		this.accesor=accesor;
	}

	@Override
	public FilterData rewriteFilter(FilterData filterData,
			CatalogExcecutionContext context, CatalogDescriptor catalog) throws Exception {
		// a user can only LIST (see without previous knowledge of a
		// specific entry) those entries he own's as a stakeholder
		if(context.getRequest().getSession().isMaster()){
			return filterData;
		}else{
			
			long personId =  context.getRequest().getSession().getStakeHolder().longValue();
			FilterCriteria stakeHolderCriteria = filterData.fetchCriteria(HasStakeHolder.STAKE_HOLDER_FIELD);
			if(stakeHolderCriteria==null){
			/*	stakeHolderCriteria = FilterDataUtils. newFilterCriteria();
				stakeHolderCriteria.setOperator(FilterData.EQUALS);
				stakeHolderCriteria.setValue(personId);
				stakeHolderCriteria.pushToPath(HasStakeHolder.STAKE_HOLDER_FIELD);*/
			}else{
				log.debug("[REWRITE STAKEHOLDER CRITERIA]={}",personId);
				stakeHolderCriteria.setValue(personId);
			}
		}
		return filterData;
	}

	@Override
	public void maybeBlockEntry(CatalogEntry entry,
			CatalogExcecutionContext context, CatalogDescriptor catalog) throws Exception {
		//FIXME scenario examen seminario, group chats
		//  a user by default may read those entries he knows, and mention
		// the user by stakeHolder or any other Person field
		if(context.getRequest().getSession().isMaster()){
			
		}else{
			long personId = context.getRequest().getSession().getStakeHolder().longValue();
			Collection<Long> people;
			Long person;
			boolean accesible = entry instanceof HasAccesablePropertyValues;
			Session session =null;
			Collection<FieldDescriptor> fields = catalog.getFieldsValues();
			for(FieldDescriptor field : fields){
				if(field.isKey() && !field.isEphemeral() &&Person.CATALOG.equals(field.getForeignCatalogName())){
					if(field.isMultiple()){
						if(accesible){
							people = (Collection<Long>) ((HasAccesablePropertyValues)entry).getPropertyValue(field.getFieldId());
						}else{
							if(session==null){
								session = accesor.newSession(entry);
							}
							people = (Collection<Long>) accesor.getPropertyValue(catalog, field, entry, null, session);
						}
						if(people!=null){
							if(!people.contains(personId)){
								throw new SecurityException("Entry "+entry.getIdAsString()+"@"+catalog.getCatalogId()+" does not reference current user");
							}
						}
					}else{
						if(accesible){
							person = (Long) ((HasAccesablePropertyValues)entry).getPropertyValue(field.getFieldId());
						}else{
							if(session==null){
								session = accesor.newSession(entry);
							}
							person = (Long) accesor.getPropertyValue(catalog, field, entry, null, session);
						}
						
						if(person!=null){
							if(person.longValue()!=personId){
								throw new SecurityException("Entry "+entry.getIdAsString()+"@"+catalog.getCatalogId()+" does not reference current user");
							}
						}
					}
				}
			}
		}
	}

}
