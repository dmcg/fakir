package com.oneeyedmen.fakir.internal;

import com.oneeyedmen.fakir.Faker;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.List;


public class FakeAccess implements Invokable {

    private static final Boolean DEFAULT_BOOLEAN = Boolean.FALSE;
    private static final Byte DEFAULT_BYTE = (byte) 0x07;
    private static final Short DEFAULT_SHORT = (short) 6;
    private static final Character DEFAULT_CHAR = '!';
    private static final Integer DEFAULT_INTEGER = 42;
    private static final Long DEFAULT_LONG = 54L;
    private static final Float DEFAULT_FLOAT = (float) Math.E;
    private static final Double DEFAULT_DOUBLE = Math.PI;
    private static final String DEFAULT_STRING = "banana";

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
        return createA(returnType);
    }

    private Object createA(Type type) {
        Class<?> rawType = type instanceof ParameterizedType ? (Class)((ParameterizedType) type).getRawType() : (Class) type;
        if (rawType == String.class)
            return DEFAULT_STRING;
        if (rawType == Boolean.TYPE || rawType == Boolean.class)
            return DEFAULT_BOOLEAN;
        if (rawType == Integer.TYPE || rawType == Integer.class)
            return DEFAULT_INTEGER;
        if (rawType == Long.TYPE || rawType == Long.class)
            return DEFAULT_LONG;
        if (rawType == Float.TYPE || rawType == Float.class)
            return DEFAULT_FLOAT;
        if (rawType == Double.TYPE || rawType == Double.class)
            return DEFAULT_DOUBLE;
        if (rawType == Character.TYPE || rawType == Character.class)
            return DEFAULT_CHAR;
        if (rawType == Byte.TYPE || rawType == Byte.class)
            return DEFAULT_BYTE;
        if (rawType == Short.TYPE || rawType == Byte.class)
            return DEFAULT_SHORT;
        if (List.class.isAssignableFrom(rawType))
            return new FakeList(firstGenericParameterOf(type));
        return Faker.fakeA(rawType);
    }

    private Class<?> firstGenericParameterOf(Type type) {
        Type thing = ((ParameterizedType) type).getActualTypeArguments()[0];
        return (Class<?>) thing;
    }


    private class FakeList extends AbstractList {

        private final Class genericType;

        public FakeList(Class genericType) {
            this.genericType = genericType;
        }

        @Override
        public Object get(int index) {
            if (index >= size())
                throw new IndexOutOfBoundsException("" + index);
            return createA(genericType);
        }

        @Override
        public int size() {
            return 3;
        }
    }
}
