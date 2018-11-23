package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.event.server.domain.impl.EvaluationContext;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ListIterator;

@Singleton
public class NaturalLanguageInterpretImpl implements NaturalLanguageInterpret{
    protected Logger log = LogManager.getLogger(NaturalLanguageInterpretImpl.class);

    public static final String ASSIGNATION = "=";

    private final FieldAccessStrategy access;

    @Inject
    public NaturalLanguageInterpretImpl(FieldAccessStrategy access) {
        this.access = access;
    }

    @Override
    public void resolve(ListIterator<String> sentence, Context t, String interpretGivenName) throws Exception {
        if(sentence.hasNext()){
            String rawValue = sentence.next();

            if(log.isDebugEnabled()){
                log.debug("   parameter: "+rawValue);
            }



            int endTokenIndex = rawValue.indexOf(':');
            Class<?> parameterType;
            HasResult<Object> holder = (HasResult<Object>) t;
            if(endTokenIndex>0){
                String accessor = rawValue.substring(0,endTokenIndex);
                rawValue = rawValue.substring(endTokenIndex+1);
                log.trace("      parsing parameter {} with accesor {}",rawValue,accessor);
                Object r ;
                if(accessor.equals("ctx")){
                    r= t.get(rawValue);
                    if(r==null){
                        log.info("      No Context key {}",rawValue);
                    }else{
                        log.trace("      Context Value: {}",r);
                    }
                }else if(accessor.equals("int")){
                    r= Integer.parseInt(rawValue);
                    log.trace("      Explicit conversion output: {}",r);
                }else if(accessor.contains(".")){
                    parameterType = Class.forName(accessor);
                    r= ConvertUtils.convert(rawValue,parameterType);
                    log.trace("      Explicit conversion output: {}",r);
                }else{
                    String cannonicalName = "java.lang."+accessor;
                    parameterType = Class.forName(cannonicalName);
                    r= ConvertUtils.convert(rawValue,parameterType);
                    log.trace("      Implicit conversion output: {}",r);
                }

                holder.setResult(r);
            }else if(rawValue.equals("null")){
                holder.setResult(null);
            }else{
                holder.setResult(rawValue);// ConvertUtils.convert(rawValue,parameterType);
            }


           /* if(t instanceof JavaNativeInterfaceContext){

            }

            if(t instanceof EvaluationContext){

            }

            if(t instanceof ApplicationContext){

            }*/

        }
    }



}
