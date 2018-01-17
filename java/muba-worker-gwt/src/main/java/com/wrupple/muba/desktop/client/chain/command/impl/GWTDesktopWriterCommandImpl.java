package com.wrupple.muba.desktop.client.chain.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.bpm.domain.DomainSystemProperties;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.server.service.SharedContextWriter;
import com.wrupple.muba.catalogs.server.service.WruppleServerModule;
import com.wrupple.muba.catalogs.server.service.WruppleServerModuleRegistry;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.server.domain.impl.DesktopRequestContextImpl;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;
import com.wrupple.vegetate.server.services.ObjectMapper;
import org.apache.commons.chain.Context;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class GWTDesktopWriterCommandImpl implements GWTDesktopWriterCommand {
    private static final String GWT_META_PROPERTY = "gwt:property";
    private static final String APPLE_SUPPORT = "apple-mobile-web-app-capable";
    private static final String APPLE_BARSTYLE = "apple-mobile-web-app-status-bar-style";
    private static final String SMALL_SCREEN_PARAMETER = "muba:reduced_screen_size";


    private final String datePattern;
    private final Provider<WruppleServerModuleRegistry> registry;
    private final ObjectMapper mapper;

    @Inject
    public GWTDesktopWriterCommandImpl(ObjectMapper mapper, @Named("datePattern") String datePattern, Provider<WruppleServerModuleRegistry> registry) {
        this.datePattern = datePattern;
        this.registry = registry;
        this.mapper = mapper;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        DesktopRequestContextImpl parameter = (DesktopRequestContextImpl) context;
        DomainSystemProperties systemProps = (DomainSystemProperties) parameter.getCatalogContext().getDomainContext().getSystemSettings();
        HttpServletResponse resp = parameter.getResponse();
        HttpServletRequest req = parameter.getRequest();
        resp.setCharacterEncoding(parameter.getCharacterEncoding());
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        // DOCTYPE
        writer.print("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n\r");
        // html
        writer.print("<html>\n\r");
        // head
        writer.print("<head>\n\r");
        writeHead(writer, parameter, systemProps, req);
        writer.print("</head>\n\r");
        // body
        System.err.println("Writing body");
        writer.print("<body>\n\r");
        writeBody(writer, parameter);
        writer.print("</body>\n\r");

        // close html
        writer.print("</html>\n\r");
        writer.close();
        return CONTINUE_PROCESSING;
    }


    private void writeHead(PrintWriter writer, DesktopRequestContextImpl parameter, DomainSystemProperties system,
                           HttpServletRequest req) throws Exception {
        // title
        writer.print("<title>");
        writer.print(parameter.getDesktopTitle());
        writer.print("</title>");
        // Content Encoding
        writer.print("<meta http-equiv=\"content-type\" content=\"text/html; charset=");
        writer.print(parameter.getCharacterEncoding());
        writer.print("\" />\n");
        // CSS
        String[] csss = parameter.getStaticDesktopCssURI();
        if (csss == null) {
        } else {
            for (int i = 0; i < csss.length; i++) {
                writer.print("\n<link type=\"text/css\" rel=\"stylesheet\" href=\"");
                writer.print(csss[i]);
                writer.print("\" />");
            }
        }
        /*
		 * String[] inlineCss = parameter.getInlineCss(); if (inlineCss == null)
		 * { } else { for (int i = 0; i < inlineCss.length; i++) { writer.print(
		 * "\n<style type=\"text/css\" >"); writer.print(inlineCss[i]);
		 * writer.print("\n</style>"); } }
		 */
        // JavaScript
        String[] jsss = parameter.getStaticDesktopJavaScriptURI();
        if (jsss != null) {
            for (int i = 0; i < jsss.length; i++) {
                writer.print("\n<script type=\"text/javascript\" language=\"javascript\" src=\"");
                writer.print(jsss[i]);
                writer.print("\"></script>");
            }
        }
		/*
		 * jsss = parameter.getInlineJavascript(); if (jsss == null) { } else {
		 * for (int i = 0; i < jsss.length; i++) { writer.print(
		 * "\n<script type=\"text/javascript\" language=\"javascript\" >");
		 * writer.print(jsss[i]); writer.print("\n</script>"); } }
		 */
		/*
		 * Evaluation context variable factory functions
		 */

        writer.println("<script type=\"text/javascript\">");

        List<String> globalExpressions = system.getGlobalContextExpressions();
        if (globalExpressions == null) {
            globalExpressions = new ArrayList<String>();
        } else {
            globalExpressions = new ArrayList<String>(globalExpressions);
        }

        //know the "agent type"
        SharedContextWriter contextWriter = new com.wrupple.muba.desktop.shared.services.impl.HtmlContextWriter(parameter.getPeerValue(), mapper, writer, globalExpressions, parameter.getDomain());
        WruppleServerModule[] modules = registry.get().getModules();
        if (modules != null) {
            for (WruppleServerModule module : modules) {
                module.writeClientContext(contextWriter);
            }
        }
        ApplicationItem dpss = parameter.getDesktopPlaceHierarchy();
        if (dpss != null) {
            contextWriter.writeUnregisteredVariable(DesktopManager.APPLICATION_HIERARCHY, dpss);
        }
        contextWriter.writeUnregisteredVariable(ReadDesktopMetadata.GLOBAL_CONTEXT, globalExpressions);
        writer.println("</script>");

        // iOS
        if (isClientIOSDevice(req)) {
            writeMetaProperty(writer, APPLE_SUPPORT, "yes");
            writeMetaProperty(writer, APPLE_BARSTYLE, "black");
            writer.print("<link rel=\"apple-touch-icon\" href=\"/favicon.ico\" />");
        }
        writer.println("<meta name='mobile-web-app-capable' content='yes'>");
        writer.println("<meta name='viewport' id='vp' content='initial-scale=1.0,user-scalable=no,maximum-scale=1,width=device-width,height=device-height' />");

        // Stupid, stupid, popular and stupid internet explorer
        if (parameter.isInternetExplorer()) {
            writer.print("<meta http-equiv=\"X-UA-Compatible\" content=\"chrome=1\" />");
            writer.print("<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/chrome-frame/1/CFInstall.min.js\"></script>");
            // TODO MATH JS REQUIRED LIBRARYES
        }
        //
        writeMetaProperty(writer, GWT_META_PROPERTY, "locale=" + parameter.getCatalogContext().getLocale());

        // TODO publish in catalog manifest
        writeMetaProperty(writer, "datePattern", datePattern);

        // small Screen Device
        if (isSmallScreenDevice()) {
            writeMetaProperty(writer, SMALL_SCREEN_PARAMETER, "true");
        }
		/*
		 * TODO REPLACE ALL THESE VARIABLES WITH EVALUATION CONTEXT EXPRESSIONS?
		 */

        // Home
        writeMetaProperty(writer, ReadDesktopMetadata.DEFAULT_HOME_ACTIVITY, parameter.getHomeActivity());
        writeMetaProperty(writer, ReadDesktopMetadata.DOMAIN_SETUP_FLAG, ReadDesktopMetadata.DOMAIN_SETUP_FLAG);

        // user preferences
        if (parameter.getCatalogContext().getDomainContext().getCurrencyCode() != null) {
            writeMetaProperty(writer, ReadDesktopMetadata.DEFAULT_CURRENCY_CODE, parameter.getCatalogContext().getDomainContext().getCurrencyCode());
        }

        if (system.getHomeImage() != null) {
            writeMetaProperty(writer, DesktopManager.HOME_BUTTON, system.getHomeImage());
        }
        if (system.getActivityPresenterToolbarHeight() != null) {
            writeMetaProperty(writer, HumanTaskWindow.ActivityPresenterToolbarHeight, system.getActivityPresenterToolbarHeight());
        }

        if (parameter.getMetaParameters() != null) {
            Map<String, String> map = parameter.getMetaParameters();
            Set<String> keys = map.keySet();
            String value;
            for (String name : keys) {
                value = map.get(name);
                writeMetaProperty(writer, name, value);
            }
        }

        System.err.println("FINISHED HEAD");
    }

    private boolean isSmallScreenDevice() {
        // TODO configure desktop to optimize task rendering in small creen
        // devices
        return false;
    }

    private boolean isClientIOSDevice(HttpServletRequest parameter) {
        String userAgent = parameter.getHeader("user-agent");
        return userAgent.contains("iPhone") || userAgent.contains("iPad");
    }

    private void writeBody(PrintWriter writer, DesktopRequestContextImpl parameter) {
        if (parameter.isInternetExplorer()) {
            writer.print(
                    "<!-- Your internet browser sucks, i hope you read this, fell bad about it, and change your twisted, evil ways. This is not 1995.--> \n\r");
            writer.print("<!--[if IE]><script>");
            writer.print("window.attachEvent(\"onload\", function() {CFInstall.check({mode: \"overlay\"});});");
            writer.print("</script><![endif]-->");
        }
        writer.print("<iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" style='position: absolute; width: 0; height: 0; border: 0'></iframe>");
    }

    private void writeMetaProperty(PrintWriter writer, String name, String value) {
        openMetaTag(writer, name);
        if (value.contains("'")) {
            writer.print("content=\"");
            writer.print(value);
            writer.print("\" />\n\r");
        } else {
            writer.print("content=\'");
            writer.print(value);
            writer.print("\' />\n\r");
        }

    }

    private void openMetaTag(PrintWriter writer, String name) {
        writer.print("\n<meta name=\"");
        writer.print(name);
        writer.print("\" ");
    }


}