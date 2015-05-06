```java

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
        public OrderStatus getStatus() { return something(); }
        public void setStatus(OrderStatus status) {}
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

    public enum OrderStatus {
        PLACED, PICKED, DISPATCHED, RECEIVED, RETURNED
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

    @Test public void enums_default_to_the_first() {
        Order order = Faker.fakeA(Order.class);
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test public void operations_are_ignored() {
        Order order = Faker.fakeA(Order.class);
        order.setStatus(OrderStatus.DISPATCHED);
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test public void but_you_can_override_properties_with_fields() {
        Customer customer = new Faker<Customer>() {
            String firstName = "fred";
            int age = 24;
        }.get();
        assertEquals("fred", customer.getFirstName());
        assertEquals("lastName", customer.getLastName());
        assertEquals(24, customer.age());
    }

    @Test public void and_if_you_do_they_are_remembered() {
        Order order = new Faker<Order>() {
            OrderStatus status = OrderStatus.RECEIVED;
        }.get();
        assertEquals(OrderStatus.RECEIVED, order.getStatus());

        order.setStatus(OrderStatus.RETURNED);
        assertEquals(OrderStatus.RETURNED, order.getStatus());
    }

    @Test public void object_properties_are_faked_recursively_and_cached() {
        Customer customer = Faker.fakeA(Customer.class);
        Address address = customer.getAddress();
        assertEquals("postcode", address.getPostcode());

        assertSame(address, customer.getAddress());
        assertNotSame(address, Faker.fakeA(Customer.class).getAddress());
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

    @Test public void you_can_install_your_own_factory() {
        DefaultFactory factory = new DefaultFactory() {
            protected BigDecimal createBigDecimal() {
                return new BigDecimal("99.99");
            }

            protected <T> List<T> createList(Class<T> genericType) {
                return new FakeList<T>(1, genericType, this);
            }
        };

        Customer customer = Faker.fakeA(Customer.class, factory);
        assertEquals(BigDecimal.valueOf(99.99), customer.getOrders().get(0).getShippingCost());
        assertEquals(1, customer.getOrders().size());
    }

    @Test public void and_of_course_combine_that_with_overrides() {
        DefaultFactory factory = new DefaultFactory() {
            protected BigDecimal createBigDecimal() {
                return new BigDecimal("99.99");
            }
        };

        Customer customer = new Faker<Customer>(Customer.class, factory) {
            int age = 101;
        }.get();
        assertEquals(101, customer.age());
        assertEquals(BigDecimal.valueOf(99.99), customer.getOrders().get(0).getShippingCost());
    }

    public interface CustomerService {
        Customer find(Long id);
    }

    @Test public void and_as_we_lean_so_heavily_on_jmock_we_give_back_an_action() {
        Mockery mockery = new Mockery();
        final CustomerService customers = mockery.mock(CustomerService.class);
        mockery.checking(new Expectations() {{
            allowing(customers); will(ReturnAFakeAction.returnAFake());
        }});

        assertEquals("postcode", customers.find(99L).getAddress().getPostcode());
    }

```
