package com.oneeyedmen.fakir;

import org.junit.Test;

import static org.junit.Assert.*;

public class FakeSetTest {

    public static class TestItem {
        public int value = 0;
    }

    private final FakeSet<TestItem> set = new FakeSet<TestItem>(2, TestItem.class, Faker.DEFAULT_FACTORY);

    @Test
    public void it_has_as_many_elements_as_its_size() {
        FakeListTest.checkSizeThroughIteration(set, 2);
    }

    @Test
    public void it_caches_its_items() {
        TestItem item0 = set.iterator().next();
        item0.value = 99;
        assertEquals(99, set.iterator().next().value);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void it_acts_like_a_set() {
        TestItem item0 = set.iterator().next();
        assertTrue(set.contains(item0));
        assertFalse(set.contains(Faker.DEFAULT_FACTORY.createA(TestItem.class)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void it_is_immutable() {
        set.add(new TestItem());
    }
}
