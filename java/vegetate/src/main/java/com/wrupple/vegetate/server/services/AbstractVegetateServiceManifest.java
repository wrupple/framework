package com.wrupple.vegetate.server.services;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Context;

import com.wrupple.vegetate.domain.VegetateServiceManifest;


public abstract class AbstractVegetateServiceManifest implements VegetateServiceManifest  {
	
	private final ObjectMapper mapper;
	private String serviceId;
	private final Class<?> contractClass;
	
	public AbstractVegetateServiceManifest(ObjectMapper mapper,Class<?> contractClass) {
		super();
		this.mapper = mapper;
		this.contractClass=contractClass;
	}

	@Override
	public final Object createExcecutionContext(Object requestContext, String[] tokenValues, String serializedContext) throws Exception{
		Context context = createBlankContext((RequestScopedContext)requestContext);
		if(contractClass!=null&serializedContext!=null){
			Object contract = mapper.readValue(serializedContext, contractClass);
			BeanUtils.copyProperties(context, contract);
		}
		String[] tokens = getUrlPathParameters();
		if(tokens!=null&&tokenValues!=null){
			for(int i =0; i < tokens.length && i< tokenValues.length;i++){
				context.put(tokens[i], tokenValues[i]);
			}
		}
		return context;
	}
	

	protected abstract Context createBlankContext(RequestScopedContext requestContext) ;

	@Override
	public String getServiceId() {
		if(serviceId==null){
			serviceId= getServiceName()+"-"+getServiceVersion();
		}
		return serviceId;
	}

}
