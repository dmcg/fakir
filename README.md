
Fakir - The Ascetic Wonder-Worker
=================================

Fake difficult-to-build objects with default property values and custom overrides.

[ExampleTest](src/test/java/com/oneeyedmen/okeydoke/examples/ApprovalsRuleTest.java)

```java

    // We've all got classes like this, coupled for good reasons, really
    // hard to build.

    public static class Customer {
        public Customer(Long id, String firstName, String lastName, Date dob, Address address, List<Order> orders) {
            //
        }

        public Long id() { return something(); }
        public String getFirstName() { return something(); }
        public String getLastName() { return something(); }
        public int rank() { return something(); }
        public Address getAddress() { return something(); }
        public List<Order> getOrders() { return something(); }
        public void printOn(PrintStream s) { s.println("Hello"); }
        public Customer getAffiliate() { return something(); }
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
        assertEquals(42, customer.rank());
        assertEquals(Long.valueOf(54), customer.id());
    }

    @Test public void enums_default_to_the_first() {
        Order order = Faker.fakeA(Order.class);
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test public void you_can_override_properties_with_fields() {
        Customer customer = new Faker<Customer>() {
            String firstName = "fred";
            int rank = 24;
        }.get();
        assertEquals("fred", customer.getFirstName());
        assertEquals("lastName", customer.getLastName());
        assertEquals(24, customer.rank());
    }

    @Test public void and_fake_operations_with_methods() {
        Customer customer = new Faker<Customer>() {
            void printOn(PrintStream s) {
                s.println("kumquat");
            }
        }.get();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        customer.printOn(new PrintStream(os));
        assertEquals("kumquat\n", os.toString());
    }

    @Test public void operations_are_ignored_unless_overridden() {
        Order order = Faker.fakeA(Order.class);
        order.setStatus(OrderStatus.DISPATCHED);
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test public void but_you_can_back_properties_with_fields() {
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
            int rank = 101;
        }.get();
        assertEquals(101, customer.rank());
        assertEquals(BigDecimal.valueOf(99.99), customer.getOrders().get(0).getShippingCost());
    }

    @Test public void you_can_explicitly_return_fakes_from_fakes() {
        Customer customer = new Faker<Customer>() {
            Address address = new Faker<Address>() {
                String line1 = "10 Downing St";
            }.get();
        }.get();
        assertEquals("10 Downing St", customer.getAddress().getLine1());
        assertEquals("postcode", customer.getAddress().getPostcode());
    }

    @Test public void and_if_you_do_you_can_use_the_fields_from_any_old_object() {
        Customer customer = new Faker<Customer>() {
            Object address = new Object() {
                String line1 = "10 Downing St";
            };
        }.get();
        assertEquals("10 Downing St", customer.getAddress().getLine1());
        assertEquals("line2", customer.getAddress().getLine2());
    }

    @Test public void which_leads_to_another_way_of_overriding_at_the_top_level() {
        Customer customer = Faker.wrapWith(Customer.class,
            new Object() {
                String firstName = "fred";
        });
        assertEquals("fred", customer.getFirstName());
    }

    @Test public void this_can_be_extended_to_create_spies_on_existing_instances() {
        // Feature for next release
        // Customer existingCustomer = Faker.fakeA(Customer.class);
        //
        // Customer customer = Faker.spyOn(existingCustomer,
        //     new Object() {
        //         String firstName = "fred";
        //     });
        // assertEquals("fred", customer.getFirstName());
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

Fakir builds and runs under JDK6, but now has some [Java8'y goodness](java8/src/test/java/com/oneeyedmen/fakir/java8/Java8ExampleTest.java)
for easy testing of legacy code.

```java

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

```
