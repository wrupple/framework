package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ParentServiceManifest;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.Dispatch;
import com.wrupple.muba.event.server.chain.command.RequestInterpret;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

@Singleton
public class DispatchImpl implements Dispatch {
    private static final Logger log = LoggerFactory.getLogger(DispatchImpl.class);

    @Override
    public boolean execute(Context context) throws Exception {
        log.debug("<{}>", this.getClass().getSimpleName());

        RuntimeContext requestContext = (RuntimeContext) context;


        if (requestContext.getSession().hasPermissionsToProcessContext(requestContext,
                requestContext.getServiceManifest())) {

            log.trace("excecution permission GRANTED for request {}, transaction will begin on {}",
                    requestContext.getId(), requestContext.getServiceManifest().getServiceId());
            if (CONTINUE_PROCESSING == incorporateContract(requestContext)) {
                Command serviceHandler = requestContext.getEventBus().getIntentInterpret().getDictionaryFactory()
                        .getCatalog(ParentServiceManifest.NAME)
                        .getCommand(requestContext.getServiceManifest().getServiceId());
                log.debug("delegating to service handler {}", serviceHandler);
                boolean r = serviceHandler.execute(requestContext.getServiceContext());
                log.debug("</{}>", this.getClass().getSimpleName());
                return r;
            } else {
                log.error("could not understand contract");
                log.debug("</{}>", this.getClass().getSimpleName());
                return PROCESSING_COMPLETE;
            }

        } else {
            log.error("Permission to process request denied");
            log.debug("</{}>", this.getClass().getSimpleName());
            return PROCESSING_COMPLETE;
        }

    }

    private boolean incorporateContract(RuntimeContext requestContext) throws Exception {

        List<String> tokens = requestContext.getServiceManifest().getGrammar();
        RequestInterpret explicitInterpret = requestContext.getEventBus().getIntentInterpret().getExplicitIntentInterpret(requestContext);
        Object contract = requestContext.getServiceContext();
        Context context = materializeContext(requestContext,
                /* we know it's this class:registration is private */ explicitInterpret);
        String key, value;
        if (explicitInterpret == null) {
            log.debug("no explicit contract interpret");
            if (tokens != null && requestContext.hasNext()) {
                log.trace("read-copy known grammar properties into service context");

                for (int i = 0; i < tokens.size(); i++) {
                    key = tokens.get(i);
                    if (requestContext.hasNext()) {
                        value = requestContext.next();
                        log.trace("[service parameter] {}={}", key, value);
                        context.put(key, value);
                    }
                }
            }

            if (contract != null && contract != context) {
                ContractDescriptor descriptor = requestContext.getServiceManifest().getCatalogValue();
                if (descriptor != null) {
                    Collection<String> fields = descriptor.getFieldsIds();
                    Object v;
                    log.trace("read-copy contract  properties into service context");

                    for (String field : fields) {
                        v = PropertyUtils.getProperty(contract, field);
                        if (v == null) {
                            log.trace("ignoring empty contract field {}", field);
                        } else {
                            log.trace("[service parameter] {}={}", field, v);

                            context.put(field, v);
                        }

                    }
                }
            }


        } else {
            log.debug("delegating to explicit contract interpret {}", explicitInterpret);
            return explicitInterpret.execute(requestContext);

        }

        return CONTINUE_PROCESSING;
    }

    private Context materializeContext(RuntimeContext requestContext, RequestInterpret explicitInterpret) throws InvocationTargetException, IllegalAccessException {
        Context context = requestContext.getServiceContext();

        if (context == null) {
            if (explicitInterpret == null) {
                log.warn("Using request context space to excecute service");
                context = requestContext;
            } else {
                context = explicitInterpret.materializeBlankContext(requestContext);
            }

        }
        requestContext.setServiceContext(context);
        return context;
    }
}
