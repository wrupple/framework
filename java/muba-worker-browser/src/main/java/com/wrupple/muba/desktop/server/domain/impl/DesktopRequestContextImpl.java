package com.wrupple.muba.desktop.server.domain.impl;

import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.DataStoreManager;
import com.wrupple.muba.catalogs.server.service.impl.CatalogUserTransaction;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.DesktopRequestContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.worker.domain.BPMPeer;
import com.wrupple.vegetate.server.services.SessionContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;

import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesktopRequestContextImpl extends ServletWebContext implements DesktopRequestContext {
    private static final long serialVersionUID = -931377061670181518L;

    private final CatalogExcecutionContext catalogContext;
    /**
     * kid-sniffing-glue.jpeg
     */
    private final boolean internetExplorer;
    private final List<String> searchEngines;
    private final String userAgent;
    private final Provider<DataStoreManager> dsmp;
    /*
     * Services
     */
    DataStoreManager dsm;
    private Boolean searchEngine;
    private String homeActivity, peer;
    private String characterEncoding;
    private String[] staticDesktopCssURI;
    private String[] staticDesktopJavaScriptURI;
    private String desktopTitle;
    private Map<String, String> metaParameters;
    private ApplicationItem desktopPlaceHierarchy;
    private boolean setupFlag;
    private List<String> warnings;
    private BPMPeer peerValue;
    private String[] pathTokens;
    private String submitUrl;
    private ProcessTaskDescriptor submitTask;
    private int nextPathToken;
    private int firstTokenIndex;
    private ApplicationItem activity;
    private ProcessTaskDescriptor task;
    private List<ApplicationItem> breadCrumbs;


    private DesktopRequestContextImpl(Provider<DataStoreManager> dsmp, CatalogExcecutionContext catalog, ServletContext ctx, HttpServletRequest req,
                                      HttpServletResponse resp, @Named("desktop.seo.userAgents") List searchAgents) {
        super(ctx, req, resp);
        this.dsmp = dsmp;
        this.catalogContext = catalog;
        this.userAgent = req.getHeader("User-Agent");
        this.internetExplorer = userAgent.contains("MSIE");
        this.searchEngines = searchAgents;
        if (this.searchEngines == null) {
            this.searchEngine = false;
        }
    }

    public DesktopRequestContextImpl(RuntimeContext requestContext) {
    }

    public ApplicationItem getActivity() {
        return activity;
    }

    public void setActivity(ApplicationItem activity) {
        this.activity = activity;
    }

    public ProcessTaskDescriptor getTask() {
        return task;
    }

    public void setTask(ProcessTaskDescriptor task) {
        this.task = task;
    }

    public DataStoreManager getDataStoreManager() {
        if (dsm == null) {
            dsm = dsmp.get();
        }
        return dsm;
    }

    public boolean isSearchEngine() {
        if (searchEngine == null) {
            for (String token : searchEngines) {
                if (userAgent.contains(token)) {
                    this.searchEngine = true;
                    return searchEngine;
                }
            }
        }
        return searchEngine;
    }

    public String getHomeActivity() {
        return homeActivity;
    }

    public void setHomeActivity(String homeActivity) {
        this.homeActivity = homeActivity;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public boolean isInternetExplorer() {
        return internetExplorer;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * @param characterEncoding the characterEncoding to set
     */
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public String[] getStaticDesktopCssURI() {
        return staticDesktopCssURI;
    }

    /**
     * @param staticDesktopCssURI the staticDesktopCssURI to set
     */
    public void setStaticDesktopCssURI(String[] staticDesktopCssURI) {
        this.staticDesktopCssURI = staticDesktopCssURI;
    }

    public String[] getStaticDesktopJavaScriptURI() {
        return staticDesktopJavaScriptURI;
    }

    /**
     * @param staticDesktopJavaScriptURI the staticDesktopJavaScriptURI to set
     */
    public void setStaticDesktopJavaScriptURI(String[] staticDesktopJavaScriptURI) {
        this.staticDesktopJavaScriptURI = staticDesktopJavaScriptURI;
    }

    public String getDesktopTitle() {
        return desktopTitle;
    }

    /**
     * @param desktopTitle the desktopTitle to set
     */
    public void setDesktopTitle(String desktopTitle) {
        this.desktopTitle = desktopTitle;
    }

    public void addParameter(String name, String value) {
        if (getMetaParameters() == null) {
            setMetaParameters(new HashMap<String, String>());
        }
        getMetaParameters().put(name, value);
    }

    public Map<String, String> getMetaParameters() {
        return metaParameters;
    }

    public void setMetaParameters(Map<String, String> metaParameters) {
        this.metaParameters = metaParameters;
    }

    public ApplicationItem getDesktopPlaceHierarchy() {
        return desktopPlaceHierarchy;
    }

    public void setDesktopPlaceHierarchy(ApplicationItem domainRoot) {
        this.desktopPlaceHierarchy = domainRoot;
    }

    public boolean isSetupFlag() {
        return setupFlag;
    }

    public void setSetupFlag(boolean b) {
        this.setupFlag = b;
    }

    @Override
    public String getFormat() {
        return "html";
    }

    @Override
    public CatalogUserTransaction getTransaction() {
        return getCatalogContext().getTransaction();
    }

    @Override
    public void addWarning(String string) {
        if (this.warnings == null) {
            this.warnings = new ArrayList<String>();
        }
        this.warnings.add(string);
    }

    public SessionContext getSession() {
        return getCatalogContext().getSession();
    }

    public Long getDomain() {
        return getCatalogContext().getDomainContext().getDomain();
    }

    public CatalogExcecutionContext getCatalogContext() {

        return catalogContext;
    }

    public BPMPeer getPeerValue() {
        return peerValue;
    }

    public void setPeerValue(BPMPeer desktopSession) {
        this.peerValue = desktopSession;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String[] getPathTokens() {
        return pathTokens;
    }

    public void setPathTokens(String[] pathTokens) {
        this.pathTokens = pathTokens;
    }

    public int getNextPathToken() {
        return nextPathToken;
    }

    public void setNextPathToken(int nextPathToken) {
        this.nextPathToken = nextPathToken;
    }

    public void pushBreadCrumb(ApplicationItem item) {
        if (breadCrumbs == null) {
            breadCrumbs = new ArrayList<ApplicationItem>(5);
        }
        breadCrumbs.add(item);
    }

    public List<ApplicationItem> getBreadCrumbs() {
        return breadCrumbs;
    }

    public String getSubmitUrl() {
        return submitUrl;
    }

    public void setSubmitUrl(String submitUrl) {
        this.submitUrl = submitUrl;
    }

    public ProcessTaskDescriptor getSubmitTask() {
        return submitTask;
    }

    public void setSubmitTask(ProcessTaskDescriptor submitTask) {
        this.submitTask = submitTask;
    }

    public int getFirstTokenIndex() {
        return firstTokenIndex;
    }

    public void setFirstTokenIndex(int firstTokenIndex) {
        this.firstTokenIndex = firstTokenIndex;
    }

}
