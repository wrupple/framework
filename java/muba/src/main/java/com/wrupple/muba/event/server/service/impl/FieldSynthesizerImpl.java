package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.domain.impl.EvaluationContext;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.FieldSynthesizer;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class FieldSynthesizerImpl implements FieldSynthesizer{
    protected static final Logger log = LogManager.getLogger(FieldSynthesizerImpl.class);

    private final Pattern pattern;
    private final FieldAccessStrategy access;

    @Inject
    public FieldSynthesizerImpl(@Named("template.pattern") Pattern pattern /** "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}" */, ServiceBus serviceBus, FieldAccessStrategy access) {
        this.pattern = pattern;
        this.access = access;
    }

    @Override
    public void evalTemplate(String template, PrintWriter out, String language, ServiceContext context) {
        Matcher matcher = pattern.matcher(template);
        Instrospection intro = access.newSession(null);
        if (matcher.find()) {
            matcher.reset();
            int start;
            int end;
            int currentIndex = 0;
            String rawToken;
            while (matcher.find()) {
                start = matcher.start();
                if (start > 0 && template.charAt(start) != '\\') {
                    end = matcher.end();
                    out.println(template.substring(currentIndex, start));
                    rawToken = matcher.group();
                    try {
                        out.print(synthethizeFieldValue(Arrays.asList(rawToken.split("\\.")).listIterator(), context, null, null, null, intro,context.getRuntimeContext().getServiceBus()));
                    } catch (Exception e) {
                        out.println("Error processing token : " + rawToken);
                    }
                    currentIndex = end;
                }
            }
            if (currentIndex < template.length()) {
                out.println(template.substring(currentIndex, template.length()));
            }
        } else {
            out.println(template);
        }
    }


    @Override
    public Object synthethizeFieldValue(ListIterator<String> split, Context context, CatalogEntry subject, ContractDescriptor subjectType, FieldDescriptor generated, Instrospection intro,ServiceBus serviceBus) throws Exception {
        if(split.hasNext()){
            String current = split.next();
            if(subjectType!=null&&generated!=null&&generated.isGenerated()){
                if(split.hasNext()){
                    //multiple terms to evaluate
                    NaturalLanguageInterpret interpret = serviceBus.getInterpret(current);
                    if(interpret==null){
                        return contextuallySynthethizeFieldValue(current,split,context,subject,subjectType,generated,intro);
                    }else{
                        //delegate to plugin
                        EvaluationContext mofo = new EvaluationContext(split, current, context, subject, subjectType, generated, intro);
                        interpret.resolve(split,mofo,current);
                        return mofo.getResult();
                    }
                }else{
                    return contextuallySynthethizeFieldValue(current,split,context,subject,subjectType,generated,intro);
                }

            }else{
                return current;
            }
        }
        return null;
    }

    private Object contextuallySynthethizeFieldValue(String currentToken,ListIterator<String> split, Context context, CatalogEntry subject, ContractDescriptor subjectType, FieldDescriptor generated, Instrospection intro){
        //TODO context.containsKey(currentToken) ?
        return currentToken;
    }


}
