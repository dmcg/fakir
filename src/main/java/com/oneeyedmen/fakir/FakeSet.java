package com.oneeyedmen.fakir;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;

public class FakeSet<T> extends AbstractSet<T> {

    private final List<T> list;

    public FakeSet(int size, Class<T> genericType, Factory factory) {
        this.list = new FakeList<T>(size, genericType, factory);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }
}
