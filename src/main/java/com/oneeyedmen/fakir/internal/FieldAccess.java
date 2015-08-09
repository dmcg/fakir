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
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

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
            if (invocation.getParameterCount() == 0) {
                return getScalarProperty(method);
            } else if (invocation.getParameterCount() == 1) {
                if (method.getReturnType() == void.class) {
                    return setScalarProperty(method, invocation.getParameter(0));
                } else {
                    for (Object optional : getKeyedProperty(method, invocation.getParameter(0))) {
                        return optional;
                    }
                }
            } else if (invocation.getParameterCount() == 2) {
                return setKeyedProperty(method, invocation.getParameter(0), invocation.getParameter(1));
            }

        } catch (NoSuchFieldException e) {
            // ignored
        }
        return next.invoke(invocation);
    }

    private Object getScalarProperty(Method method) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldNameForAccessor(method));
        field.setAccessible(true);
        return returnValueFor(field.get(object), method.getGenericReturnType(), method.getName());
    }

    private Object setScalarProperty(Method method, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldNameForMutator(method));
        field.setAccessible(true);
        field.set(object, value);
        return null;
    }

    private Iterable<Object> getKeyedProperty(Method method, Object key) throws NoSuchFieldException, IllegalAccessException {
        Map<?, ?> map = mapForKeyedProperty(fieldNameForAccessor(method));
        if (map.containsKey(key)) {
            return singleton(returnValueFor(map.get(key), method.getGenericReturnType(), method.getName()));
        }
        else {
            return emptyList();
        }
    }

    private Object setKeyedProperty(Method method, Object key, Object value) throws NoSuchFieldException, IllegalAccessException {
        Map map = mapForKeyedProperty(fieldNameForMutator(method));
        //noinspection unchecked
        map.put(key, value);
        return null;
    }

    private Map mapForKeyedProperty(String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return (Map) field.get(object);
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
            return removePrefixAndUncapitalise(methodName, 3);
        }
        else if (methodName.startsWith("is")) {
            return removePrefixAndUncapitalise(methodName, 2);
        }
        else {
            return methodName;
        }
    }

    private static String removePrefixAndUncapitalise(String methodName, int index) {
        return Character.toLowerCase(methodName.charAt(index)) + methodName.substring(index + 1);
    }

    private Iterable<Object> gotFromSupplier(Class<?> typeToReturn, Object o) {
        if (o instanceof Supplier<?>) {
            Object supplied = ((Supplier) o).get();
            if (canReturnAs(typeToReturn, supplied)) {
                return singleton(supplied);
            }
        }

        return emptyList();
    }

    @SuppressWarnings("unchecked")
    private Iterable<Object> gotFromJava8Supplier(Class<?> typeToReturn, Object o) {
        try {
            Class java8Supplier = Class.forName("java.util.function.Supplier");
            if (java8Supplier.isAssignableFrom(o.getClass())) {
                Object supplied = java8Supplier.getDeclaredMethod("get").invoke(o);
                if (canReturnAs(typeToReturn, supplied)) {
                    return singleton(supplied);
                }
            }
        } catch (Exception x) {
            // ignored
        }

        return emptyList();
    }
}
