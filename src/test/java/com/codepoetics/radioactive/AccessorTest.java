package com.codepoetics.radioactive;

import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

public class AccessorTest {

    public static class Address {

        public static final Accessor<Address, String> FIRST_LINE = Accessor.of(Address::getFirstLine, Address::setFirstLine);
        public static final Accessor<Address, String> SECOND_LINE = Accessor.of(Address::getSecondLine, Address::setSecondLine);
        public static final Accessor<Address, String> THIRD_LINE = Accessor.of(Address::getThirdLine, Address::setThirdLine);
        public static final Accessor<Address, String> FOURTH_LINE = Accessor.of(Address::getFourthLine, Address::setFourthLine);
        public static final Accessor<Address, String> POSTCODE = Accessor.of(Address::getPostCode, Address::setPostCode);
        public static final Builder<Address, Address> BUILDER = Builder.startingWith(Address::new);

        private String firstLine;
        private String secondLine;
        private String thirdLine;
        private String fourthLine;
        private String postCode;

        public String getFirstLine() {
            return firstLine;
        }

        public void setFirstLine(String firstLine) {
            this.firstLine = firstLine;
        }

        public String getSecondLine() {
            return secondLine;
        }

        public void setSecondLine(String secondLine) {
            this.secondLine = secondLine;
        }

        public String getThirdLine() {
            return thirdLine;
        }

        public void setThirdLine(String thirdLine) {
            this.thirdLine = thirdLine;
        }

        public String getFourthLine() {
            return fourthLine;
        }

        public void setFourthLine(String fourthLine) {
            this.fourthLine = fourthLine;
        }

        public String getPostCode() {
            return postCode;
        }

        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }
    }

    public static class Person {

        public static final Accessor<Person, String> NAME = Accessor.of(Person::getName, Person::setName);
        public static final Accessor<Person, Address> ADDRESS = Accessor.of(Person::getAddress, Person::setAddress);
        public static final Builder<Person, Person> BUILDER = Builder.startingWith(Person::new);

        private String name;
        private Address address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    private final Person person = Person.BUILDER
            .with(Person.NAME.of("Arthur Putey"),
                  Person.ADDRESS.of(Address.BUILDER
                    .with(Address.FIRST_LINE, "22 Acacia Avenue",
                          Address.SECOND_LINE, "Sunderland",
                          Address.POSTCODE, "VB6 5UX")))
            .get();

    @Test public void
    getsAProperty() {
        assertThat(Person.NAME.get(person), equalTo("Arthur Putey"));
    }

    @Test public void
    getsANestedProperty() {
        assertThat(Person.ADDRESS.join(Address.SECOND_LINE).get(person), equalTo("Sunderland"));
    }

    @Test public void
    setsAProperty() {
        Person.NAME.set(person, "Angus Yentob");

        assertThat(person.getName(), equalTo("Angus Yentob"));
    }

    @Test public void
    setsANestedProperty() {
        Person.ADDRESS.join(Address.POSTCODE).set(person, "RA8 81T");

        assertThat(person.getAddress().getPostCode(), equalTo("RA8 81T"));
    }

    @Test public void
    updatesAProperty() {
        Person.NAME.update(person, String::toUpperCase);

        assertThat(person.getName(), equalTo("ARTHUR PUTEY"));
    }

    @Test public void
    swapsAProperty() {
        assertThat(Person.NAME.swap(person, "Angus Yentob"), equalTo("Arthur Putey"));
        assertThat(person.getName(), equalTo("Angus Yentob"));
    }

    @Test public void
    getsAndUpdatesAProperty() {
        assertThat(Person.NAME.getAndUpdate(person, String::toUpperCase), equalTo("Arthur Putey"));
        assertThat(person.getName(), equalTo("ARTHUR PUTEY"));
    }

    @Test public void
    updatesAndGetsAProperty() {
        assertThat(Person.NAME.updateAndGet(person, String::toUpperCase), equalTo("ARTHUR PUTEY"));
        assertThat(person.getName(), equalTo("ARTHUR PUTEY"));
    }

    @Test public void
    nullableToOptional() {
        MaybeAccessor<Person, String> maybeName = Accessor.ofNullable(Person::getName, Person::setName);

        Person hollowMan = new Person();

        assertThat(maybeName.get(hollowMan), equalTo(Optional.empty()));

        maybeName.set(hollowMan, Optional.of("Paul Spector"));

        assertThat(hollowMan.getName(), equalTo("Paul Spector"));

        maybeName.set(hollowMan, Optional.empty());

        assertThat(hollowMan.getName(), nullValue());
    }

    @Test public void
    chainedOptionalAccessors() {
        MaybeAccessor<Person, String> maybePostcode = Accessor.ofNullable(Person::getAddress, Person::setAddress)
                .flatMap(Accessor.ofNullable(Address::getPostCode, Address::setPostCode), Address::new);

        Person hollowMan = new Person();

        maybePostcode.set(hollowMan, Optional.of("HR9 5BH"));

        assertThat(hollowMan.getAddress().getPostCode(), equalTo("HR9 5BH"));
    }

    @Test public void
    createsRef() {
        Ref<String> nameRef = Person.NAME.toRef(person);

        assertThat(nameRef.get(), equalTo("Arthur Putey"));

        nameRef.set("Angus Yentob");

        assertThat(person.getName(), equalTo("Angus Yentob"));
    }

    @Test public void
    listAccessor() {
        List<String> data = Arrays.asList("foo", "bar", "baz");
        Accessor<List<String>, String> secondItem = Accessor.forList(1);

        assertThat(secondItem.get(data), equalTo("bar"));

        secondItem.set(data, "xyzzy");

        assertThat(data, equalTo(Arrays.asList("foo", "xyzzy", "baz")));
    }

    @Test public void
    arrayAccessor() {
        String[] data = new String[] { "foo", "bar", "baz" };
        Accessor<String[], String> secondItem = Accessor.forArray(1);

        assertThat(secondItem.get(data), equalTo("bar"));

        secondItem.set(data, "xyzzy");

        assertThat(data, equalTo(new String[] { "foo", "xyzzy", "baz" }));
    }

    @Test public void
    mapAccessor() {
        Map<String, String> data = new HashMap<>();
        data.put("a", "apple");
        data.put("b", "banana");
        data.put("c", "carrot");

        MaybeAccessor<Map<String, String>, String> bIsFor = Accessor.forMap("b");

        assertThat(bIsFor.get(data), equalTo(Optional.of("banana")));

        bIsFor.set(data, Optional.of("botulism"));

        assertThat(data.get("b"), equalTo("botulism"));

        bIsFor.set(data, Optional.empty());

        assertFalse(data.containsKey("b"));
    }
}
