package com.codepoetics.radioactive;

import org.junit.Test;

import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BuilderTest {

    public static class Record {
        private final String name;
        private final int age;

        public Record(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public static class Builder implements Supplier<Record> {

            public static final Setter<Builder, String> NAME = Setter.of(Builder::withName);
            public static final Setter<Builder, Integer> AGE = Setter.of(Builder::withAge);

            private String name;
            private int age;

            public Builder withName(String name) {
                this.name = name;
                return this;
            }

            public Builder withAge(int age) {
                this.age = age;
                return this;
            }

            public Record get() {
                return new Record(name, age);
            }
        }
    }

    @Test public void
    build_with_a_builder() {
        Record record = Builder.buildingWith(Record.Builder::new)
                .with(Record.Builder::withName, "Arthur Putey",
                      Record.Builder::withAge, 30)
                .get();

        assertThat(record.getName(), equalTo("Arthur Putey"));
        assertThat(record.getAge(), equalTo(30));
    }

    @Test public void
    build_with_setters() {
        Record record = Builder.buildingWith(Record.Builder::new)
                .with(Record.Builder.NAME.of("Arthur Putey"),
                      Record.Builder.AGE.of(30))
                .get();

        assertThat(record.getName(), equalTo("Arthur Putey"));
        assertThat(record.getAge(), equalTo(30));
    }

}
