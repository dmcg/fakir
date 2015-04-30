
package com.oneeyedmen.fakir;

import com.oneeyedmen.fakir.internal.FakeAccess;
import com.oneeyedmen.fakir.internal.FieldAccess;
import com.oneeyedmen.fakir.internal.MethodAccess;
import org.jmock.api.Imposteriser;
import org.jmock.api.Invokable;
import org.jmock.internal.ProxiedObjectIdentity;
import org.jmock.lib.legacy.ClassImposteriser;

import java.lang.reflect.ParameterizedType;

public class Faker<T> {

    private static final Imposteriser IMPOSTERISER = ClassImposteriser.INSTANCE;

    @SuppressWarnings("unchecked")
    private final Class<T> type;

    public Faker(Class<T> type) {
        //noinspection unchecked
        this.type = type != null ? type :
                (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public static <T> T fakeA(Class<T> type) {
        return new Faker<T>(type).get();
    }

    protected Faker() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return IMPOSTERISER.imposterise(
                new MyProxiedObjectIdentity(
                        new MethodAccess(this,
                                new FieldAccess(this, new FakeAccess()))),
                type);
    }


    private class MyProxiedObjectIdentity extends ProxiedObjectIdentity{
        public MyProxiedObjectIdentity(Invokable next) {
            super(next);
        }

        @Override
        public String toString() {
            return "A fake " + type.getSimpleName();
        }
    }

}
