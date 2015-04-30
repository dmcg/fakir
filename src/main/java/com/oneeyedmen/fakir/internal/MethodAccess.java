package com.oneeyedmen.fakir.internal;

import org.jmock.api.Invocation;
import org.jmock.api.Invokable;

import java.lang.reflect.Method;

public class MethodAccess implements Invokable {

    private final Object object;
    private final Invokable next;

    public MethodAccess(Object object, Invokable next) {
        this.object = object;
        this.next = next;
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        try {
            Method matchingMethod = methodMatching(invocation);
            matchingMethod.setAccessible(true);
            return matchingMethod.invoke(object, invocation.getParametersAsArray());
        } catch (Exception x) {
            return next.invoke(invocation);
        }
    }

    private Method methodMatching(Invocation invocation) throws NoSuchMethodException {
        Method invokedMethod = invocation.getInvokedMethod();
        return object.getClass().getDeclaredMethod(invokedMethod.getName(), invokedMethod.getParameterTypes());
    }
}
