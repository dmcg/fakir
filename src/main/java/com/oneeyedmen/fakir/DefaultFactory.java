package com.oneeyedmen.fakir;

public class DefaultFactory extends AbstractFactory {

    @Override
    protected <T> T lastResort(Class<T> type) {
        return Faker.fakeA(type, this);
    }
}
