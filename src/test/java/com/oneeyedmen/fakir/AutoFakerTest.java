package com.oneeyedmen.fakir;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("UnusedDeclaration")
public class AutoFakerTest {

    public abstract class ClassToBeFaked {
        public abstract String name();
        public abstract String getProperty();
        public abstract char aChar();
        public abstract void operation();
        public abstract Void pathological();
        public abstract int function(int a);
    }

    private final ClassToBeFaked fake = Faker.fakeA(ClassToBeFaked.class);

    @Test public void returns_accessor_name_for_string() {
        assertEquals("name", fake.name());
        assertEquals("property", fake.getProperty());
    }

    @Test public void delegates_to_factory_for_other_property_types() {
        assertEquals('!', fake.aChar());
    }

    @Test public void ignores_parameters_for_functions() {
        assertEquals(42, fake.function(6));
    }

    @Test public void ignores_operations() {
        fake.operation();
    }
}
