package com.oneeyedmen.fakir.java8;

import com.oneeyedmen.fakir.ExampleTest.Customer;
import com.oneeyedmen.fakir.Faker;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions", "unchecked"})
public class Java8ExampleTest {

// README_TEXT

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

    @Test public void or_more_functionally() {
        Function<com.oneeyedmen.fakir.Supplier<Customer>, Supplier<Customer>> converter = s -> s::get;

        Supplier<Customer> java8Supplier = converter.apply(new Faker<Customer>() {
            String firstName = "Fred";
        });
    }

    @Test public void faker_will_delegate_to_suppliers_of_either_package() {
        Customer customer = Faker.wrapWith(Customer.class, new Object() {
            com.oneeyedmen.fakir.Supplier<String> firstName = () -> "Fred";
            Supplier<String> lastName = () -> "Flintstone";
        });
        assertEquals("Fred", customer.getFirstName());
        assertEquals("Flintstone", customer.getLastName());
    }

// README_TEXT
}
