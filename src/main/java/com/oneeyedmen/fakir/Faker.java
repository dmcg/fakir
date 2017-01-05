
package com.oneeyedmen.fakir;

import com.oneeyedmen.fakir.internal.FakeAccess;
import com.oneeyedmen.fakir.internal.FieldAccess;
import com.oneeyedmen.fakir.internal.MethodAccess;
import org.jmock.api.Imposteriser;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.internal.ProxiedObjectIdentity;
import org.jmock.lib.legacy.ClassImposteriser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Faker<T> implements Supplier<T>{

    private static final Imposteriser IMPOSTERISER = ClassImposteriser.INSTANCE;
    static final Factory DEFAULT_FACTORY = new DefaultFactory() {
        @Override
        protected <T> T lastResort(Class<T> type) {
            return fakeA(type, this);
        }
    };

    @SuppressWarnings("unchecked")
    private final Class<T> type;
    private final Factory factory;


    public static <T> T fakeA(Class<T> type) {
        return fakeA(type, DEFAULT_FACTORY);
    }

    public static <T> T fakeA(Class<T> type, Factory factory) {
        return new Faker<T>(type, factory).get();
    }

    public Faker(Class<T> type, Factory factory) {
        this.factory = factory;
        this.type = type != null ? type : guessMyType();
    }

    // constructor called when we subclass Faker
    protected Faker() {
        this(null, DEFAULT_FACTORY);
    }

    public static <T> T wrapWith(Class<T> type, Factory factory, Object delegate) {
        Invokable invokableChain = new MethodAccess(delegate,
                new MyProxiedObjectIdentity(type,
                        new FieldAccess(delegate, factory,
                                new Cacher(
                                        new FakeAccess(factory)))));
        return IMPOSTERISER.imposterise(invokableChain, type);
    }

    public static <T> T wrapWith(Class<T> type, Object delegate) {
        return wrapWith(type, DEFAULT_FACTORY, delegate);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return wrapWith(type, factory, this);
    }

    private static class MyProxiedObjectIdentity extends ProxiedObjectIdentity {

        private final Class<?> type;

        public MyProxiedObjectIdentity(Class<?> type, Invokable next) {
            super(next);
            this.type = type;
        }

        @Override
        public String toString() {
            return "A fake " + type.getSimpleName();
        }
    }

    private static class Cacher implements Invokable {
        private final Map<Invocation, Object> cache = new HashMap<Invocation, Object>();
        private final Invokable next;

        public Cacher(Invokable next) {
            this.next = next;
        }


        @Override
        public Object invoke(Invocation invocation) throws Throwable {
            Object cached = cache.get(invocation);
            if (cached != null)
                return cached;
            Object result = next.invoke(invocation);
            cache.put(invocation, result);
            return result;
        }
    }

    private Class<T> guessMyType() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = genericSuperclass.getActualTypeArguments()[0];
        if (type instanceof Class<?>) {
            //noinspection unchecked
            return (Class<T>) type;
        } else {
            throw new UnsupportedOperationException("Sorry, Fakir doesn't fake generic types");
        }
    }
}
