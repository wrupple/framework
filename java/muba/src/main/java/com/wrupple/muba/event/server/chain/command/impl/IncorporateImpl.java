package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.command.Incorporate;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.FieldSynthesizer;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class IncorporateImpl implements Incorporate {
    private static final Logger log = LogManager.getLogger(IncorporateImpl.class);

    private final FieldSynthesizer synthesizer;
    private final FieldAccessStrategy accessor;

    /**
     * set context writing precedence of the value of a property in favor of
     * what's in the sentence, over the contract
     */
    protected boolean sentenceOverContract = true;

    @Inject
    public IncorporateImpl(FieldSynthesizer synthesizer, FieldAccessStrategy accessor) {
        this.synthesizer = synthesizer;
        this.accessor = accessor;
    }

    @Override
    public boolean execute(Context context) throws Exception {

        RuntimeContext requestContext = (RuntimeContext) context;
        ContractDescriptor descriptor = requestContext.getServiceManifest().getCatalogValue();
        String key;
        Object value;
        Object contract = requestContext.getServiceContract();
        log.debug("INCORPORATING CONTRACT {}:", requestContext.getServiceManifest().getServiceId(), contract);

        if (contract == null) {
            ServiceManifest manifest = requestContext.getServiceManifest();
            if (manifest.getCatalogValue().getClazz() != null) {
                if (!manifest.getCatalogValue().getClazz().isInterface()) {
                    contract = manifest.getCatalogValue().getClazz().newInstance();
                    requestContext.setServiceContract(contract);
                }

            }
        }

        if (contract != null) {
            List<String> tokens = requestContext.getServiceManifest().getGrammar();
            log.trace("Incomming contract {}", contract);
            Instrospection instrospector=null;
            CatalogEntry entry = null;
            FieldDescriptor field = null;
            if(descriptor!=null){
                entry = (CatalogEntry) contract;
               instrospector = accessor.newSession((CatalogEntry) contract);
            }
            for (int i = 0; i < tokens.size(); i++) {
                if (requestContext.hasNext()) {
                    key = tokens.get(i);
                    if ((descriptor!=null &&
                            accessor.isWriteableProperty(key,entry,instrospector) &&
                            (accessor.getPropertyValue(key,entry,null,instrospector)==null
                                    || sentenceOverContract
                            ))||
                            (PropertyUtils.isWriteable(contract, key) &&
                                    (PropertyUtils.getProperty(contract, key) == null
                                            || sentenceOverContract
                                    )
                            ))
                    {

                        if(descriptor==null){
                            value = requestContext.next();
                            accessor.setPropertyValue(key,entry,  value ,instrospector);
                        }else{
                            field = descriptor.getFieldDescriptor(key);
                            value = synthesizer.synthethizeFieldValue(requestContext,requestContext,entry,descriptor,field,instrospector,requestContext.getServiceBus());
                            accessor.setPropertyValue(key,entry,  value ,instrospector);
                        }
                        if(log.isDebugEnabled()){
                            log.debug("instructed assignation of contract parameter {}={}", key, value);
                        }
                    } else {
                        log.info("ignored service grammar token disowned by contract:  \"{}\"", key);
                        log.trace("stoped analizing sentence");
                        break;
                    }
                }else{
                    log.debug("reached end of sentence");
                    break;
                }
            }

        }



        return CONTINUE_PROCESSING;
    }


}
