package com.oneeyedmen.fakir;

import org.junit.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public class DefaultFactoryTest {

    public abstract class AnotherClassToBeFaked {
        public abstract String thing();
        public abstract List<String> list();
    }

    private final Factory factory = DefaultFactory.INSTANCE;

    @Test public void returns_defaults_for_primitives() {
        assertEquals(false, factory.createA(Boolean.TYPE));
        assertEquals(false, factory.createA(Boolean.class));
        assertEquals((byte) 0x07, factory.createA(Byte.TYPE));
        assertEquals((byte) 0x07, factory.createA(Byte.class));
        assertEquals((short) 6, factory.createA(Short.TYPE));
        assertEquals((short) 6, factory.createA(Short.class));
        assertEquals('!', factory.createA(Character.TYPE));
        assertEquals('!', factory.createA(Character.class));
        assertEquals(42, factory.createA(Integer.TYPE));
        assertEquals(42, factory.createA(Integer.class));
        assertEquals(54L, factory.createA(Long.TYPE));
        assertEquals(54L, factory.createA(Long.class));
        assertEquals((float) Math.E, factory.createA(Float.TYPE));
        assertEquals((float) Math.E, factory.createA(Float.class));
        assertEquals(Math.PI, factory.createA(Double.TYPE));
        assertEquals(Math.PI, factory.createA(Double.class));
    }

    @Test public void returns_new_object_for_Object() {
        assertNotSame(factory.createA(Object.class), factory.createA(Object.class));
    }

    @Test public void returns_the_first_of_an_enum() {
        assertEquals(TimeUnit.values()[0], factory.createA(TimeUnit.class));
    }

    @Test public void returns_null_for_Void() {
        assertNull(factory.createA(Void.class));
    }

    @Test public void has_some_other_defaults() {
        assertEquals(BigDecimal.valueOf(6.02214129e23), factory.createA(BigDecimal.class));
    }

    @Test public void creates_a_list_of_things_when_generic_type_info_info() throws NoSuchMethodException {
        Type listOfStringType = AnotherClassToBeFaked.class.getDeclaredMethod("list").getGenericReturnType();
        List<String> list = (List<String>) factory.createA(listOfStringType);
        assertEquals(3, list.size());
        assertEquals("banana", list.get(0));
    }

    @Test public void creates_a_list_of_object_when_no_generic_type_info() {
        List<Object> list = (List<Object>) factory.createA(List.class);
        assertEquals(3, list.size());
        assertEquals(Object.class, list.get(0).getClass());
    }

    @Test public void returns_another_fake_for_others() {
        assertEquals("thing", ((AnotherClassToBeFaked)factory.createA(AnotherClassToBeFaked.class)).thing());
    }

}
