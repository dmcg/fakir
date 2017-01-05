package com.oneeyedmen.fakir;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

import java.lang.reflect.Type;

public class ReturnAFakeAction implements Action {

    private static final Action INSTANCE = returnAFakeFrom(Faker.DEFAULT_FACTORY);

    private final Factory factory;
    private final Type type;

    public static Action returnAFake() {
        return INSTANCE;
    }

    public static Action returnAFakeFrom(Factory factory) {
        return new ReturnAFakeAction(null, factory);
    }

    public static Action returnAFake(Type type) {
        return new ReturnAFakeAction(type);
    }

    public ReturnAFakeAction(Type type) {
        this(type, Faker.DEFAULT_FACTORY);
    }

    private ReturnAFakeAction(Type type, Factory factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        return factory.createA(type == null ? invocation.getInvokedMethod().getGenericReturnType() : type);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("return a fake value");
    }

}
