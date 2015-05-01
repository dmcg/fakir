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
        if (invocation.getParameterCount() == 0) {
            return resultFor(invocation);
        }
        else {
            throw new NoSuchMethodError(invocation.getInvokedMethod().getName());
        }
    }

    private Object resultFor(Invocation invocation) {
        Method invokedMethod = invocation.getInvokedMethod();
        Type returnType = invokedMethod.getGenericReturnType();
        if (returnType == Void.TYPE)
            throw new NoSuchMethodError(invocation.getInvokedMethod().getName());
        if (returnType == String.class)
            return FieldAccess.fieldNameForAccessor(invokedMethod);
        return factory.createA(returnType);
    }

}
