package com.oneeyedmen.fakir.java8;

import com.oneeyedmen.fakir.ExampleTest.Customer;
import com.oneeyedmen.fakir.Faker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static org.junit.Assert.*;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions", "unchecked"})
public class Java8ExampleTest {

// README_TEXT

    @Test public void faker_will_delegate_to_suppliers() {
        Customer customer = new Faker<Customer>() {
            Supplier<Long> id = () -> Math.round(Long.MAX_VALUE * Math.random());
        }.get();
        assertNotEquals("OK, it's contrived, but you get the point", customer.id(), customer.id());
    }

    @Test public void which_can_solve_some_circular_dependency_issues() {
        List<Customer> customers = new ArrayList<>(2);
        Object[] customerData = {
            new Object() {
                String lastName = "Flintstone";
                Supplier<Customer> affiliate = () -> customers.get(1);
            },
            new Object() {
                Supplier<String> lastName = () -> customers.get(0).getLastName();
            }
        };
        stream(customerData).map(data -> Faker.wrapWith(Customer.class, data)).forEach(customers::add);

        assertEquals("Flintstone", customers.get(0).getLastName());
        assertEquals("Flintstone", customers.get(1).getLastName());
        assertEquals(customers.get(1), customers.get(0).getAffiliate());
        assertEquals(customers.get(1), customers.get(0).getAffiliate());
    }

    @Test
    public void you_cant_cast_a_faker_to_a_Java8_supplier() {
        try {
            Faker<Customer> faker = new Faker<Customer>() {
                String firstName = "Fred";
            };
            Supplier<Customer> java8Supplier = (Supplier<Customer>) faker;
            fail("A shame, but nevermind");
        } catch (ClassCastException ignored) {
        }
    }

    @Test public void but_you_can_finesse_it() {
        Supplier<Customer> java8Supplier = new Faker<Customer>() {
            String firstName = "Fred";
        }::get;
    }

    @Test public void and_use_that_to_generalise() {
        Supplier<Customer> java8Supplier = asSupplier(new Faker<Customer>() {
            String firstName = "Fred";
        });
    }

    private <T> Supplier<T> asSupplier(com.oneeyedmen.fakir.Supplier<T> faker) {
        return faker::get;
    }

// README_TEXT
}
