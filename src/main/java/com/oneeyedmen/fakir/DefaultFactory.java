package com.oneeyedmen.fakir;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class DefaultFactory implements Factory {
    
    public static final Factory INSTANCE = new DefaultFactory();

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

    @Override
    public Object createA(Type type) {
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
            return createChat();
        if (rawType == Byte.TYPE || rawType == Byte.class)
            return createByte();
        if (rawType == Short.TYPE || rawType == Short.class)
            return createShort();
        if (List.class.isAssignableFrom(rawType))
            return createList(genericTypeFor(type));
        if (BigDecimal.class.isAssignableFrom(rawType))
            return createBigDecimal();
        if (rawType == Object.class)
            return createObject();
        return Faker.fakeA(rawType, this);
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

    protected Character createChat() {
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

    protected List createList(Class<?> genericType) {
        return new FakeList(3, genericType, this);
    }

    private Class<?> genericTypeFor(Type type) {
        if (type instanceof ParameterizedType)
            return firstGenericParameterOf((ParameterizedType) type);
        else return Object.class;
    }

    private Class<?> firstGenericParameterOf(ParameterizedType type) {
        return (Class<?>) type.getActualTypeArguments()[0];
    }

}
