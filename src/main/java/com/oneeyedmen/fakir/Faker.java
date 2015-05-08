
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
import java.util.HashMap;
import java.util.Map;

public class Faker<T> {

    private static final Imposteriser IMPOSTERISER = ClassImposteriser.INSTANCE;

    @SuppressWarnings("unchecked")
    private final Class<T> type;
    private final Factory factory;


    public static <T> T fakeA(Class<T> type) {
        return fakeA(type, DefaultFactory.INSTANCE);
    }

    public static <T> T fakeA(Class<T> type, Factory factory) {
        return new Faker<T>(type, factory).get();
    }

    public Faker(Class<T> type, Factory factory) {
        this.factory = factory;
        //noinspection unchecked
        this.type = type != null ? type :
                (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Faker() {
        this(null, DefaultFactory.INSTANCE);
    }

    public static <T> T wrapWith(Class<T> type, Factory factory, Object delegate) {
        MyProxiedObjectIdentity invokableChain = new MyProxiedObjectIdentity(type,
                new MethodAccess(delegate,
                        new FieldAccess(delegate, factory,
                                new Cacher(
                                        new FakeAccess(factory)))));
        return IMPOSTERISER.imposterise(invokableChain, type);
    }

    public static <T> T wrapWith(Class<T> type, Object delegate) {
        return wrapWith(type, DefaultFactory.INSTANCE, delegate);
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
}
