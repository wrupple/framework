package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.ParentServiceManifest;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.YieldContext;
import com.wrupple.muba.event.server.chain.command.BindService;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class BindServiceImpl implements BindService {

    private static final Logger log = LoggerFactory.getLogger(BindServiceImpl.class);


    @Override
    public boolean execute(Context context) throws Exception {
        log.debug("<{}>", this.getClass().getSimpleName());

        RuntimeContext requestContext = (RuntimeContext) context;


        if (requestContext.hasNext()) {
            if (log.isDebugEnabled()) {
                log.info("[ SENTENCE] {}", requestContext.getSentence().subList(requestContext.nextIndex(),
                        requestContext.getSentence().size()));
            }
        } else {
            log.error("Excecution interpret iterator is at the end of the sentence.");
        }

        String service = requestContext.next();
        ServiceManifest manifest = getChildServiceManifest(requestContext.getServiceBus().getIntentInterpret().getRootService(),service, requestContext);
        requestContext.setServiceManifest(manifest);

        log.debug("</{}>", this.getClass().getSimpleName());


        return CONTINUE_PROCESSING;
    }


    private ServiceManifest getChildServiceManifest(ParentServiceManifest rootService,String service, YieldContext requestContext) {
        if (rootService == null) {
            throw new IllegalStateException("No root service has been configured");
        }
        Map<String, ServiceManifest> versions = rootService.getVersions(service);
        if (versions == null) {
            log.warn("unknown service \"{}\", attempting fallback", service);
            if (rootService.getFallbackService() == null) {
                log.error("unknown service \"{}\" and no fallbaack", service);
                throw new IllegalArgumentException("No such service :" + service);
            }
            log.info("serving \"{}\" with fallback service handler {}", service, rootService.getFallbackService());
            requestContext.setNextWordIndex(requestContext.nextIndex() - 1);
            return rootService.getFallbackService();
        } else {
            ServiceManifest manifest;

            log.trace("service invoked :{}", service);

            // 1 version
            if (requestContext.hasNext()) {
                String version = requestContext.next();
                manifest = versions.get(version);
                if (manifest == null) {
                    log.warn("Service version \"{}\" not found. Falling back to default version.", version);
                    requestContext.setNextWordIndex(requestContext.nextIndex() - 1);
                    manifest = versions.values().iterator().next();
                } else {
                    log.trace("service version :{}", version);
                }

            } else {
                log.trace("using default version of service");
                manifest = versions.values().iterator().next();
            }
            return manifest;
        }
    }
}
