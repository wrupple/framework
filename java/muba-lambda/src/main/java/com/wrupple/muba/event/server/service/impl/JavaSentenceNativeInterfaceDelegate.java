package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.server.chain.command.impl.JavaSentenceNativeInterface;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
public class JavaSentenceNativeInterfaceDelegate implements JavaSentenceNativeInterface.Delegate {
    public Collection<Method> findMethod(Method[] methods, String methodName, int parameterCount) {
        return Arrays.
                stream(methods).
                filter(method -> method.getName().equals(methodName)).
                filter(method -> parameterCount < 0 || method.getParameterCount() == parameterCount).
                collect(Collectors.toList());
    }

    public Collection<Method> findMethod(Method[] methods, String methodName) {
        return Arrays.
                stream(methods).
                filter(method -> method.getName().equals(methodName)).
                collect(Collectors.toList());
    }
}
