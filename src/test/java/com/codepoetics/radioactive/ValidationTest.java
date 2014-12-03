package com.codepoetics.radioactive;

import org.junit.Test;

import static com.codepoetics.radioactive.Validators.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

public class ValidationTest {

    public static class Person {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    @Test public void
    successful_validation() {
        Validator<Person> validator = Validator.validating("name", Person::getName, equalTo("Agnes Phobos"))
                .and("age", Person::getAge, greaterThanOrEqualTo(0));

        Valid<Person> validPerson = Valid.validate(new Person("Agnes Phobos", 43), validator);
        assertTrue(validPerson.isValid());
        assertTrue(validPerson.get().isPresent());
    }


    @Test public void
    unsuccessful_validation() {
        Validator<Person> validator = Validator
                .validating("name", Person::getName, equalTo("Agnes Phobos"))
                .and("age", Person::getAge, greaterThanOrEqualTo(0));

        Valid<Person> validPerson = Valid.validate(new Person("Angus Eros", -2), validator);

        assertFalse(validPerson.isValid());
        assertFalse(validPerson.get().isPresent());
        assertThat(validPerson.validationErrors(), hasItems("name: Angus Eros != Agnes Phobos", "age: -2 < 0"));
    }

    @Test public void
    short_circuiting_validation() {
        Validator<Person> validator = Validator
            .validating("name", Person::getName, notNull(equalTo("Agnes Phobos")));

        assertThat(Valid.validate(new Person(null, 0), validator).validationErrors(), hasItem("name: must not be null"));
    }


}
