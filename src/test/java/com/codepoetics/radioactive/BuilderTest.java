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

    @Test
    public void buildWithABuilder() {
        Record record = Builder.buildingWith(Record.Builder::new, Record.Builder::get)
                .with(Record.Builder::withName, "Arthur Putey",
                      Record.Builder::withAge, 30)
                .get();

        assertThat(record.getName(), equalTo("Arthur Putey"));
        assertThat(record.getAge(), equalTo(30));
    }

}
