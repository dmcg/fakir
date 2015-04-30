package com.oneeyedmen.fakir;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("UnusedDeclaration")
public class ExampleTest {

    /*
     * Fakir - The Ascetic Wonder-Worker
     *
     * Fake difficult to build objects, defaulting property values and
     * allowing overrides.
     */

    // We've all got classes like this, coupled for good reasons, really
    // hard to build.

    public static class Customer {
        public Customer(Long id, String firstName, String lastName, Date dob, Address address, List<Order> orders) {
            //
        }

        public Long id() { return something(); }
        public String getFirstName() { return something(); }
        public String getLastName() { return something(); }
        public int age() { return something(); }
        public Address getAddress() { return something(); }
        public List<Order> getOrders() { return something(); }
        public void printOn(PrintStream s) { s.println("Hello"); }
    }

    public static class Address {
        public Address(Long id, String line1, String line2, String postcode) {
            // ...
        }

        public Long id() { return something(); }
        public String getLine1() { return something(); }
        public String getLine2() { return something(); }
        public String getPostcode() { return something(); }
    }

    public static class Order {
        public Order(Long id, Customer customer, Address shippedTo, BigDecimal shippingCost, List<OrderItem> items) {
            // ...
        }

        public Long id() { return something(); }
        public Customer getCustomer() { return something(); }
        public Address getShippedTo() { return something(); }
        public BigDecimal getShippingCost() { return something(); }
    }

    public static class OrderItem {
        public OrderItem(Long id, Order order, Product product, BigDecimal quantity, BigDecimal net, BigDecimal gross, String notes) {
            //...
        }
        //...
    }

    public abstract class Product {
        public Product(Long id, String description, Object ... andSoOnAndSoOn) {}
    }

    // To build a Customer, you need an Address, some Orders, some OrderItems, some Products...

    // Fakir to the rescue!

    @Test public void fakir_can_build_you_a_customer_in_one_line() {
        Customer customer = Faker.fakeA(Customer.class);
        assertTrue(customer instanceof Customer);
    }

    @Test public void it_fakes_string_properties_to_return_their_name() {
        Customer customer = Faker.fakeA(Customer.class);
        assertEquals("firstName", customer.getFirstName());
        assertEquals("lastName", customer.getLastName());
    }

    @Test public void primitive_properties_have_defaults_too() {
        Customer customer = Faker.fakeA(Customer.class);
        assertEquals(42, customer.age());
        assertEquals(Long.valueOf(54), customer.id());
    }

    @Test public void you_can_override_properties_with_fields() {
        Customer customer = new Faker<Customer>() {
            String firstName = "fred";
            int age = 24;
        }.get();
        assertEquals("fred", customer.getFirstName());
        assertEquals("lastName", customer.getLastName());
        assertEquals(24, customer.age());
    }

    @Test public void object_properties_are_faked_too() {
        Customer customer = Faker.fakeA(Customer.class);
        assertEquals("postcode", customer.getAddress().getPostcode());
    }

    @Test public void lists_are_faked_with_3_entries() {
        Customer customer = Faker.fakeA(Customer.class);
        assertEquals(3, customer.getOrders().size());
        assertEquals("line1", customer.getOrders().get(1).getShippedTo().getLine1());
    }

    @Test public void you_can_fake_operations_with_methods() {
        Customer customer = new Faker<Customer>() {
            void printOn(PrintStream s) {
                s.println("kumquat");
            }
        }.get();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        customer.printOn(new PrintStream(os));
        assertEquals("kumquat\n", os.toString());
    }


    private static <T> T something() {
        return null;
    }

}
