package com.oneeyedmen.fakir.internal;

import com.oneeyedmen.fakir.Factory;
import com.oneeyedmen.fakir.Faker;
import com.oneeyedmen.fakir.Supplier;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

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
        for (Object optional : gotFromSupplier(rawType, o)) {
            return optional;
        };
        for (Object optional : gotFromJava8Supplier(rawType, o)) {
            return optional;
        };
        try {
            return Faker.wrapWith(rawType, factory, o);
        } catch (Exception fallback) {
            throw new ClassCastException("Faker couldn't construct a " + type + " for " + fieldName);
        }
    }

    private boolean canReturnAs(Class<?> rawType, Object o) {
        return o == null || rawType.isInstance(o) || isBoxedInstanceOf(rawType, o);
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

    private List gotFromSupplier(Class<?> typeToReturn, Object o) {
        if (o instanceof Supplier<?>) {
            Object supplied = ((Supplier) o).get();
            if (canReturnAs(typeToReturn, supplied)) {
                return Collections.singletonList(supplied);
            }
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    private List gotFromJava8Supplier(Class<?> typeToReturn, Object o) {
        try {
            Class java8Supplier = Class.forName("java.util.function.Supplier");
            if (java8Supplier.isAssignableFrom(o.getClass())) {
                Object supplied = java8Supplier.getDeclaredMethod("get").invoke(o);
                if (canReturnAs(typeToReturn, supplied))
                    return Collections.singletonList(supplied);
            }
        } catch (Exception x) {

        }
        return Collections.EMPTY_LIST;
    }
}
