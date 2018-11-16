package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.impl.JavaNativeInterfaceContext;
import com.wrupple.muba.event.server.chain.command.SentenceNativeInterface;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class JavaSentenceNativeInterface implements SentenceNativeInterface {
    protected Logger log = LogManager.getLogger(JavaSentenceNativeInterface.class);

    private final Delegate delgeate;


    @Inject
    public JavaSentenceNativeInterface(Delegate delgeate) {
        this.delgeate = delgeate;
    }

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

            Collection<Method> possiblemethods;
            if(parameterCount>0){
                possiblemethods = delgeate.findMethod(methods, methodName, parameterCount);
            } else {
                possiblemethods = delgeate.findMethod(methods, methodName);
            }
            if(possiblemethods.isEmpty()){
                throw new IllegalArgumentException("no matches for method "+methodName+" in "+subjectType.getCanonicalName());
            }
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
        }
        return CONTINUE_PROCESSING;
    }

    public interface Delegate {
        Collection<Method> findMethod(Method[] methods, String methodName, int parameterCount);

        Collection<Method> findMethod(Method[] methods, String methodName);
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
                        log.info("parameter {} of type {} is expected to be {}",i,v.getClass(),parameterTypes[i]);
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
        }else if(aClass.equals(Boolean.class)){
            return parameterType.equals(boolean.class);
        }
        return false;
    }


}
