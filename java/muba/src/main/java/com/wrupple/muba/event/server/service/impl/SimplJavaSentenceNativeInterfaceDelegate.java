package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.server.chain.command.impl.JavaSentenceNativeInterface;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
public class SimplJavaSentenceNativeInterfaceDelegate implements JavaSentenceNativeInterface.Delegate {
    @Override
    public Collection<Method> findMethod(Method[] methods, String methodName, int parameterCount) {
        List<Method> r = new ArrayList<>();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                if (m.getParameterTypes().length == parameterCount) {
                    r.add(m);
                }

            }
        }

        return r;
    }

    @Override
    public Collection<Method> findMethod(Method[] methods, String methodName) {
        List<Method> r = new ArrayList<>();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                r.add(m);
            }
        }

        return r;
    }
}
