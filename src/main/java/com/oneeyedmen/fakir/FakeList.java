package com.oneeyedmen.fakir;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

public class FakeList<T> extends AbstractList<T> {

    private final Class<T> genericType;
    private final Factory factory;
    private final int size;
    private final Map<Integer, T> cache = new HashMap<Integer, T>();

    public FakeList(int size, Class<T> genericType, Factory factory) {
        this.size = size;
        this.genericType = genericType;
        this.factory = factory;
    }

    @Override
    public T get(int index) {
        if (index >= size())
            throw new IndexOutOfBoundsException("" + index);
        T cached = cache.get(index);
        if (cached != null)
            return cached;
        T result = (T) factory.createA(genericType);
        cache.put(index, result);
        return result;
    }

    @Override
    public int size() {
        return size;
    }
}
