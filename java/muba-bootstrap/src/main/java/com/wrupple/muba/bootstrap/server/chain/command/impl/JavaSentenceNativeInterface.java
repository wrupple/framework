package com.wrupple.muba.bootstrap.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.JavaNativeInterfaceContext;
import com.wrupple.muba.bootstrap.server.chain.command.SentenceNativeInterface;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class JavaSentenceNativeInterface implements SentenceNativeInterface {
    protected Logger log = LoggerFactory.getLogger(JavaSentenceNativeInterface.class);

    @Override
    public boolean execute(Context ctx) throws Exception {
        JavaNativeInterfaceContext context = (JavaNativeInterfaceContext) ctx;

        if(context.sentenceIterator.hasNext()){
            log.trace("invoking native java api");
            Class<?> subjectType = context.subject.getClass();
            Method[] methods = subjectType.getMethods();
            ListIterator<String> sentenceIterator = context.sentenceIterator;
            String methodName = sentenceIterator.next();
            String firstToken = sentenceIterator.next();
            List<String> parmeters = null;
            if(firstToken.equals("(")){
                log.trace("gethering all explicitely bound parameters");
                parmeters= getherParameters(sentenceIterator);
                if(log.isTraceEnabled()){
                    log.trace(parmeters.toString());
                }
            }else{
                sentenceIterator.previous();
                parmeters = null;
            }

            int parameterCount = parmeters ==null ? -1 :parmeters.size();
            if(parameterCount==-1){
                parmeters=getherParameters(sentenceIterator);
            }


            Stream<Method> matches = Arrays.stream(methods).filter(method ->method.getName().equals(methodName));

            if(parameterCount>0){
                matches = matches.filter(method ->method.getParameterCount()==parameterCount);
            }
            Collection<Method> possiblemethods = matches.collect(Collectors.toList());

            for(Method method : possiblemethods) {
                sentenceIterator = parmeters.listIterator();
                log.info("attempting native method {} ",methodName);
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] parameterValues = new Object[parameterTypes.length];
                String rawValue;
                for(int i = 0 ; i < parameterTypes.length; i++){
                    if(sentenceIterator.hasNext()){
                        rawValue = sentenceIterator.next();
                        parameterValues[i] =parseParameter(rawValue,parameterTypes[i],context);
                    }else{
                        log.debug("        not enough tokens in sentence to satisfy method parameter count demand");
                    }
                }
                if(allMatch(parameterTypes,parameterValues)) {
                    if(log.isTraceEnabled()){
                        log.trace("method    : {} ",method);
                        log.trace("parameters: {} ",Arrays.toString(parameterValues));
                    }
                    context.result=method.invoke(context.subject,parameterValues);
                    break;
                }
            }
            if(context.result==null){
                throw new IllegalArgumentException("no matches for method "+methodName+" in "+subjectType.getCanonicalName());
            }


        }
        return CONTINUE_PROCESSING;
    }

    private List<String> getherParameters(ListIterator<String> sentenceIterator) {
        List<String> regreso = new ArrayList<>();
        String token;
        while(sentenceIterator.hasNext()){
            token = sentenceIterator.next();
            if(token.equals(")")){
                return regreso;
            }else{
                regreso.add(token);
            }
        }
        return regreso;
    }

    private boolean allMatch(Class<?>[] parameterTypes, Object[] parameterValues) {
        if(parameterTypes.length==parameterValues.length){
            for(int i = 0; i < parameterValues.length; i++) {
                Object v = parameterValues[i];
                if(v!=null && (!parameterTypes[i].isAssignableFrom(v.getClass()))){
                    if(!isCompatiblePrimitive(parameterTypes[i],v.getClass())){
                        log.debug("parameter {} of type {} is expected to be {}",i,v.getClass(),parameterTypes[i]);
                        return false;
                    }
                }
            }
        }else{
            return false;
        }
        return true;
    }

    private boolean isCompatiblePrimitive(Class<?> parameterType, Class<?> aClass) {
        log.trace("¿ {} == {} ?",parameterType,aClass);
        if(aClass.equals(Integer.class)){
            return parameterType.equals(int.class);
        }
        return false;
    }

    private Object parseParameter(String rawValue, Class<?> parameterType, JavaNativeInterfaceContext context) throws ClassNotFoundException {
        if(log.isDebugEnabled()){
            log.debug("   parameter: "+rawValue);
        }
        int endTokenIndex = rawValue.indexOf(':');
        if(endTokenIndex>0){
            String accessor = rawValue.substring(0,endTokenIndex);
            rawValue = rawValue.substring(endTokenIndex+1);
            log.trace("      parsing parameter {} with accesor {}",rawValue,accessor);
            Object r ;
            if(accessor.equals("ctx")){
                r= context.get(rawValue);
                log.trace("      Context Value: {}",r);
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
            return r;
        }else if(rawValue.equals("null")){
            return null;
        }else{
            return ConvertUtils.convert(rawValue,parameterType);
        }
    }


}
