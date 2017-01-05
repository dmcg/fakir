package com.oneeyedmen.fakir;

import org.junit.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public class DefaultFactoryTest {

    public abstract class ClassToBeFaked {
        public abstract String thing();
        public abstract List<String> list();
        public abstract Set<String> set();
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

    @Test public void creates_a_list_of_things_when_generic_type_info_available() throws NoSuchMethodException {
        Type listOfStringType = ClassToBeFaked.class.getDeclaredMethod("list").getGenericReturnType();
        List<String> list = (List<String>) factory.createA(listOfStringType);
        assertEquals(DefaultFactory.DEFAULT_COLLECTION_SIZE, list.size());
        assertEquals("banana", list.get(0));
    }

    @Test public void creates_a_list_of_object_when_no_generic_type_info_available() {
        List<Object> list = (List<Object>) factory.createA(List.class);
        assertEquals(2, list.size());
        assertEquals(Object.class, list.get(0).getClass());
    }

    @Test public void creates_a_set_of_things_when_generic_type_info_available() throws NoSuchMethodException {
        Type setOfStringType = ClassToBeFaked.class.getDeclaredMethod("set").getGenericReturnType();
        Set<String> set = (Set<String>) factory.createA(setOfStringType);
        assertEquals(2, set.size());
        // OK, this is a bit weird
        Iterator<String> iterator = set.iterator();
        assertEquals("banana", iterator.next());
        assertEquals("banana", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test public void creates_an_array_of_object() {
        ClassToBeFaked[] array = (ClassToBeFaked[]) factory.createA(ClassToBeFaked[].class);
        assertEquals(2, array.length);
        assertEquals("thing", array[0].thing());
    }

    @Test public void creates_an_array_of_string() {
        String[] array = (String[]) factory.createA(String[].class);
        assertEquals(2, array.length);
        assertEquals("banana", array[0]);
    }

    @Test public void creates_an_array_of_primitive() {
        int[] array = (int[]) factory.createA(int[].class);
        assertEquals(2, array.length);
        assertEquals(42, array[0]);
    }

    @Test public void returns_another_fake_for_others() {
        assertEquals("thing", ((ClassToBeFaked)factory.createA(ClassToBeFaked.class)).thing());
    }

    @Test public void allows_override_by_class() {
        DefaultFactory factory = new DefaultFactory().withOverride(ClassToBeFaked.class,
                new Faker<ClassToBeFaked>() {
                    String thing = "bob";
                }.get());
        assertEquals("bob", ((ClassToBeFaked) factory.createA((Type) ClassToBeFaked.class)).thing());
    }

    @Test public void allows_override_by_class_with_a_faker() {
        DefaultFactory factory = new DefaultFactory().withOverride(ClassToBeFaked.class,
                new Faker<ClassToBeFaked>() {
                    String thing = "bob";
                });
        assertEquals("bob", ((ClassToBeFaked) factory.createA((Type) ClassToBeFaked.class)).thing());
    }

    @Test public void allows_override_by_class_with_an_object() {
        DefaultFactory factory = new DefaultFactory().withOverrideObject(ClassToBeFaked.class,
                new Object() {
                    String thing = "bob";
                });
        assertEquals("bob", ((ClassToBeFaked) factory.createA((Type) ClassToBeFaked.class)).thing());
    }

}
