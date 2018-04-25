package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.server.chain.command.Incorporate;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class IncorporateImpl implements Incorporate {
    private static final Logger log = LogManager.getLogger(IncorporateImpl.class);

    /**
     * set context writing precedence of the value of a property in favor of
     * what's in the sentence, over the contract
     */
    protected boolean sentenceOverContract = true;

    @Override
    public boolean execute(Context context) throws Exception {

        RuntimeContext requestContext = (RuntimeContext) context;
        String key, value;
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
            for (int i = 0; i < tokens.size(); i++) {
                key = tokens.get(i);

                if (PropertyUtils.isWriteable(contract, key)
                        && (PropertyUtils.getProperty(contract, key) == null || sentenceOverContract)) {

                    if (requestContext.hasNext()) {
                        value = requestContext.next();
                    } else {
                        value = null;
                    }
                    if (value != null) {
                        log.debug("service grammar defined contract key {}={}", key, value);

                        BeanUtilsBean2.getInstance().setProperty(contract, key, value);
                    }
                } else {
                    log.error("token \"{}\" from service grammar was not recognized by contract and was ignored ", key);
                    log.trace("stop analizing sentence");
                    requestContext.setNextWordIndex(requestContext.previousIndex());
                    break;
                }

            }

        }



        return CONTINUE_PROCESSING;
    }


}
