package com.oneeyedmen.fakir;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("UnusedDeclaration")
public class AutoFakerTest {

    public abstract class ClassToBeFaked {
        public abstract String name();
        public abstract String getProperty();
        public abstract boolean aBoolean();
        public abstract AnotherClassToBeFaked getChild();
        public abstract byte aByte();
        public abstract short aShort();
        public abstract char aChar();
        public abstract int anInt();
        public abstract long aLong();
        public abstract float aFloat();
        public abstract double aDouble();
        public abstract Integer anInteger();

        public abstract void operation();

        public abstract Void pathological();

        public abstract int function(int a);

        public abstract List<String> aList();
        public abstract List<AnotherClassToBeFaked> getChildren();
    }

    public abstract class AnotherClassToBeFaked {
        public abstract String thing();
        public abstract int getValue();
    }

    private final ClassToBeFaked fake = Faker.fakeA(ClassToBeFaked.class);

    @Test public void return_accessor_name_for_string() {
        assertEquals("name", fake.name());
        assertEquals("property", fake.getProperty());
    }

    @Test public void return_defaults_for_primitives() {
        assertEquals(false, fake.aBoolean());
        assertEquals(0x07, fake.aByte());
        assertEquals(6, fake.aShort());
        assertEquals('!', fake.aChar());
        assertEquals(42, fake.anInt());
        assertEquals(54, fake.aLong());
        assertEquals(Math.E, fake.aFloat(), 0.00001);
        assertEquals(Math.PI, fake.aDouble(), 0.00001);
    }

    @Test public void return_defaults_for_boxed() {
        assertEquals(Integer.valueOf(42), fake.anInteger());
    }

    @Test public void return_a_list() {
        assertEquals(3, fake.aList().size());
        assertEquals("banana", fake.aList().get(0));

        assertEquals("thing", fake.getChildren().get(2).thing());
    }

    @Test public void return_another_fake_for_others() {
        assertEquals("thing", fake.getChild().thing());
        assertEquals(42, fake.getChild().getValue());
    }

    @Test public void operations() {
        try {
            fake.operation();
            fail();
        } catch (NoSuchMethodError expected) {}
        try {
            fake.function(6);
            fail();
        } catch (NoSuchMethodError expected) {}
    }

    @Test public void pathological() {
        try {
            fake.pathological();
            fail();
        } catch (IllegalArgumentException expected) {}
    }

}
