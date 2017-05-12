package com.wrupple.muba.bootstrap.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.JavaNativeInterfaceContext;
import com.wrupple.muba.bootstrap.server.chain.command.SentenceNativeInterface;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by rarl on 11/05/17.
 */
public class JavaSentenceNativeInterface implements SentenceNativeInterface {
    protected Logger log = LoggerFactory.getLogger(JavaSentenceNativeInterface.class);

    @Override
    public boolean execute(Context ctx) throws Exception {
        JavaNativeInterfaceContext context = (JavaNativeInterfaceContext) ctx;

        if(context.sentenceIterator.hasNext()){
            String methodName = context.sentenceIterator.next();
            Class<?> subjectType = context.subject.getClass();
            Method[] methods = subjectType.getDeclaredMethods();
            Optional<Method> match = Arrays.stream(methods).filter(method -> method.getName().equals(methodName)).findAny();
            if(match.isPresent()){
                log.info(methodName);
                Class<?>[] parameterTypes = match.get().getParameterTypes();
                Object[] parameterValues = new Object[parameterTypes.length];
                String rawValue;
                for(int i = 0 ; i < parameterTypes.length; i++){
                    if(context.sentenceIterator.hasNext()){
                        rawValue = context.sentenceIterator.next();
                        if(log.isInfoEnabled()){
                            log.info("   parameter: "+rawValue);
                        }
                        parameterValues[i] =ConvertUtils.convert(rawValue,parameterTypes[i]);
                    }else{
                        throw new IllegalArgumentException("not enough tokens in sentence to satisfy method parameter count demand");
                    }
                }

                context.result=match.get().invoke(context.subject,parameterValues);
            }else{
                throw new IllegalArgumentException("no such method :"+methodName+" in "+subjectType.getCanonicalName());
            }

        }
        return CONTINUE_PROCESSING;
    }
}
