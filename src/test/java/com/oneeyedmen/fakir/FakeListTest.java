package com.oneeyedmen.fakir;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class FakeListTest {

    public static class TestItem {
        public int value = 0;
    }

    private final FakeList<TestItem> list = new FakeList<TestItem>(2, TestItem.class, DefaultFactory.INSTANCE);

    @Test
    public void it_has_as_many_elements_as_its_size() {
        assertEquals(2, list.size());
        assertNotNull(list.get(0));
        assertNotNull(list.get(1));
        try {
            list.get(2);
            fail();
        } catch (IndexOutOfBoundsException expected) {}
    }

    @Test
    public void it_iterates_with_as_many_elements_as_its_size() {
        checkSizeThroughIteration(list, 2);
    }

    @Test
    public void it_caches_its_items() {
        list.get(0).value = 99;
        assertEquals(99, list.get(0).value);
        assertNotEquals(list.get(0), list.get(1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void it_is_immutable() {
        list.add(new TestItem());
    }

    public static void checkSizeThroughIteration(Iterable<?> iterable, int size) {
        Iterator<?> iterator = iterable.iterator();
        for (int i = 0; i < size; i++) {
            assertTrue(iterator.hasNext());
            assertNotNull(iterator.next());
        }
        assertFalse(iterator.hasNext());
    }
}
