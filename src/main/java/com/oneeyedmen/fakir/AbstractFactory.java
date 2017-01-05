package com.oneeyedmen.fakir;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFactory implements Factory {

    public static final int DEFAULT_COLLECTION_SIZE = 2;

    public static final Boolean DEFAULT_BOOLEAN = Boolean.FALSE;
    public static final Byte DEFAULT_BYTE = (byte) 0x07;
    public static final Short DEFAULT_SHORT = (short) 6;
    public static final Character DEFAULT_CHAR = '!';
    public static final Integer DEFAULT_INTEGER = 42;
    public static final Long DEFAULT_LONG = 54L;
    public static final Float DEFAULT_FLOAT = (float) Math.E;
    public static final Double DEFAULT_DOUBLE = Math.PI;
    public static final String DEFAULT_STRING = "banana";
    public static final BigDecimal DEFAULT_BIG_DECIMAL = BigDecimal.valueOf(6.02214129e23);

    private final Map<Type, Supplier<?>> overrides = new HashMap<Type, Supplier<?>>();

    @Override
    public Object createA(Type type) {
        Supplier<?> overrideOrNull = overrideOrNullFor(type);
        if (overrideOrNull != null)
            return overrideOrNull.get();
        Class<?> rawType = type instanceof ParameterizedType ? (Class)((ParameterizedType) type).getRawType() : (Class) type;
        if (rawType == String.class)
            return createString();
        if (rawType == Boolean.TYPE || rawType == Boolean.class)
            return createBoolean();
        if (rawType == Integer.TYPE || rawType == Integer.class)
            return createInt();
        if (rawType == Long.TYPE || rawType == Long.class)
            return createLong();
        if (rawType == Float.TYPE || rawType == Float.class)
            return createFloat();
        if (rawType == Double.TYPE || rawType == Double.class)
            return createDouble();
        if (rawType == Character.TYPE || rawType == Character.class)
            return createChar();
        if (rawType == Byte.TYPE || rawType == Byte.class)
            return createByte();
        if (rawType == Short.TYPE || rawType == Short.class)
            return createShort();
        if (rawType.isArray())
            return createArray(rawType);
        if (List.class.isAssignableFrom(rawType))
            return createList(genericTypeFor(type));
        if (Set.class.isAssignableFrom(rawType))
            return createSet(genericTypeFor(type));
        return createA(rawType);
    }

    private <T> Object createArray(Class<T> rawType) {
        Class<?> componentType = rawType.getComponentType();
        return componentType.isPrimitive() ? createPrimitiveArray(componentType) : createReferenceArray(componentType);
    }

    protected Object createReferenceArray(Class<?> componentType) {
        List<?> list = createList(componentType);
        return list.toArray((Object[]) Array.newInstance(componentType, list.size()));
    }

    protected <T> Object createPrimitiveArray(Class<T> componentType) {
        Object result = Array.newInstance(componentType, DEFAULT_COLLECTION_SIZE);
        for (int i = 0; i < DEFAULT_COLLECTION_SIZE; i++) {
            Array.set(result, i, createA((Type) componentType));
        }
        return result;
    }

    private Supplier<?> overrideOrNullFor(Type type) {
        return overrides.get(type);
    }

    protected <T> Object createA(Class<T> type) {
        if (BigDecimal.class.isAssignableFrom(type))
            return createBigDecimal();
        if (Enum.class.isAssignableFrom(type))
            return createEnum(type);
        if (type == Object.class)
            return createObject();
        if (type == Void.class)
            return null;
        return lastResort(type);
    }

    protected abstract <T> T lastResort(Class<T> type);

    private Enum createEnum(Class<?> type) {
        try {
            return ((Enum[]) type.getDeclaredMethod("values").invoke(null))[0];
        } catch (Exception e) {
            throw new RuntimeException("Can't find the first " + type);
        }
    }

    private Object createObject() {
        return new Object();
    }

    protected BigDecimal createBigDecimal() {
        return DEFAULT_BIG_DECIMAL;
    }

    protected Short createShort() {
        return DEFAULT_SHORT;
    }

    protected Byte createByte() {
        return DEFAULT_BYTE;
    }

    protected Character createChar() {
        return DEFAULT_CHAR;
    }

    protected Double createDouble() {
        return DEFAULT_DOUBLE;
    }

    protected Float createFloat() {
        return DEFAULT_FLOAT;
    }

    protected Long createLong() {
        return DEFAULT_LONG;
    }

    protected Integer createInt() {
        return DEFAULT_INTEGER;
    }

    protected Boolean createBoolean() {
        return DEFAULT_BOOLEAN;
    }

    protected String createString() {
        return DEFAULT_STRING;
    }

    protected <T> List<T> createList(Class<T> genericType) {
        return new FakeList<T>(DEFAULT_COLLECTION_SIZE, genericType, this);
    }

    protected <T> Set<T> createSet(Class<T> genericType) {
        return new FakeSet<T>(DEFAULT_COLLECTION_SIZE, genericType, this);
    }

    private Class<?> genericTypeFor(Type type) {
        if (type instanceof ParameterizedType)
            return firstGenericParameterOf((ParameterizedType) type);
        else return Object.class;
    }

    private Class<?> firstGenericParameterOf(ParameterizedType type) {
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    public <T> AbstractFactory withOverride(Class<T> type, T value) {
        overrides.put(type, new FixedValueObjectSupplier<T>(value));
        return this;
    }

    public <T> AbstractFactory withOverride(Class<T> type, Faker<T> faker) {
        overrides.put(type, faker);
        return this;
    }

    public <T> AbstractFactory withOverrideObject(Class<T> type, Object supplier) {
        return withOverride(type, Faker.wrapWith(type, this, supplier));
    }

    private class FixedValueObjectSupplier<T> implements Supplier<T> {
        private final T value;

        public FixedValueObjectSupplier(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }
    }
}
