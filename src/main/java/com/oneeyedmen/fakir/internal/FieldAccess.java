package com.oneeyedmen.fakir.internal;

import com.oneeyedmen.fakir.Factory;
import com.oneeyedmen.fakir.Faker;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class FieldAccess implements Invokable {

    private final Object object;
    private final Factory factory;
    private final Invokable next;

    public FieldAccess(Object object, Factory factory, Invokable next) {
        this.object = object;
        this.factory = factory;
        this.next = next;
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        Method method = invocation.getInvokedMethod();
        try {
            if (method.getParameterTypes().length == 0) {
                Field field = object.getClass().getDeclaredField(fieldNameForAccessor(method));
                field.setAccessible(true);
                return returnValueFor(field.get(object), method.getGenericReturnType(), method.getName());
            } else {
                Field field = object.getClass().getDeclaredField(fieldNameForMutator(method));
                field.setAccessible(true);
                field.set(object, invocation.getParametersAsArray()[0]);
                return null;
            }
        } catch (NoSuchFieldException e) {
            return next.invoke(invocation);
        }
    }

    private Object returnValueFor(Object o, Type type, String fieldName) {
        Class<?> rawType = type instanceof ParameterizedType ? (Class)((ParameterizedType) type).getRawType() : (Class) type;
        if (canReturnAs(rawType, o))
            return o;
        try {
            return Faker.wrapWith(rawType, factory, o);
        } catch (Exception fallback) {
            throw new ClassCastException("Faker couldn't construct a " + type.getTypeName() + " for " + fieldName);
        }
    }

    private boolean canReturnAs(Class<?> rawType, Object o) {
        return rawType.isInstance(o) || isBoxedInstanceOf(rawType, o);
    }

    private boolean isBoxedInstanceOf(Class<?> rawType, Object o) {
        if (o instanceof Boolean && rawType == Boolean.TYPE)
            return true;
        if (o instanceof Byte && rawType == Byte.TYPE)
            return true;
        if (o instanceof Short && rawType == Short.TYPE)
            return true;
        if (o instanceof Character && rawType == Character.TYPE)
            return true;
        if (o instanceof Integer && rawType == Integer.TYPE)
            return true;
        if (o instanceof Long && rawType == Long.TYPE)
            return true;
        if (o instanceof Float && rawType == Float.TYPE)
            return true;
        if (o instanceof Double && rawType == Double.TYPE)
            return true;
        if (o instanceof Void && rawType == Void.TYPE)
            return true;
        return false;
    }

    private String fieldNameForMutator(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("set"))
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        else
            return methodName;
    }

    static String fieldNameForAccessor(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("get")) {
            return removePrefixAnduncapitalise(methodName, 3);
        }
        else if (methodName.startsWith("is"))
            return removePrefixAnduncapitalise(methodName, 2);
        else
            return methodName;
    }

    private static String removePrefixAnduncapitalise(String methodName, int index) {
        return Character.toLowerCase(methodName.charAt(index)) + methodName.substring(index + 1);
    }
}
