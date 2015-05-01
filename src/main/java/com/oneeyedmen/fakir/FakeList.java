package com.oneeyedmen.fakir;

import java.util.AbstractList;

public class FakeList extends AbstractList {

    private final Class genericType;
    private final Factory factory;
    private final int size;

    public FakeList(int size, Class genericType, Factory factory) {
        this.size = size;
        this.genericType = genericType;
        this.factory = factory;
    }

    @Override
    public Object get(int index) {
        if (index >= size())
            throw new IndexOutOfBoundsException("" + index);
        return factory.createA(genericType);
    }

    @Override
    public int size() {
        return size;
    }
}
