package com.oneeyedmen.fakir.internal;

import com.oneeyedmen.fakir.Factory;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;


public class FakeAccess implements Invokable {

    private final Factory factory;

    public FakeAccess(Factory factory) {
        this.factory = factory;
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        return resultFor(invocation);
    }

    private Object resultFor(Invocation invocation) {
        Method invokedMethod = invocation.getInvokedMethod();
        Type returnType = invokedMethod.getGenericReturnType();
        if (returnType == String.class)
            return FieldAccess.fieldNameForAccessor(invokedMethod);
        if (returnType == Void.TYPE)
            return null;
        return factory.createA(returnType);
    }
}
