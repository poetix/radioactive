package com.codepoetics.radioactive;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MappingTest {

    @Test public void
    mapping_from_map_to_map() {
        Map<String, String> left = ImmutableMap.of("foo", "bar");
        Map<String, String> right = new HashMap<>();

        Mapper.from(Getter.<String, String>fromMap("foo"))
              .to(Setter.<String, String>toMap("xyzzy"))
              .accept(left, right);

        assertThat(right.get("xyzzy"), equalTo("bar"));
    }

    public static class Person {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Knight extends Person {
        private String quest;

        public String getQuest() {
            return quest;
        }

        public void setQuest(String quest) {
            this.quest = quest;
        }

        public static class Builder implements Supplier<Knight> {
            private String name;
            private int age;
            private String quest;

            public Builder withName(String name) {
                this.name = name;
                return this;
            }

            public Builder withAge(int age) {
                this.age = age;
                return this;
            }

            public Builder withQuest(String quest) {
                this.quest = quest;
                return this;
            }

            public Knight get() {
                Knight knight = new Knight();
                knight.setName(name);
                knight.setAge(age);
                knight.setQuest(quest);
                return knight;
            }
        }
    }

    @Test public void
    mapping_from_bean_to_same_bean_class() {
        Person person1 = new Person();
        person1.setName("Antigone");
        person1.setAge(30);

        Person person2 = Mapper.from(Person::getAge).to(Person::setAge)
              .andFrom(Person::getName).to(Person::setName)
              .creatingWith(Person::new)
              .apply(person1);

        assertThat(person2.getName(), equalTo("Antigone"));
        assertThat(person2.getAge(), equalTo(30));
    }

    @Test public void
    mapping_from_bean_to_different_bean_class() {
        Person person1 = new Person();
        person1.setName("Antigone");
        person1.setAge(30);

        Knight bean2 = Mapper.from(Person::getAge).to(Knight::setAge)
                .andFrom(Person::getName).via(String::toUpperCase).to(Knight::setName)
                .andFrom("I seek the grail").to(Knight::setQuest)
                .creatingWith(Knight::new)
                .apply(person1);

        assertThat(bean2.getName(), equalTo("ANTIGONE"));
        assertThat(bean2.getAge(), equalTo(30));
        assertThat(bean2.getQuest(), equalTo("I seek the grail"));
    }

    @Test public void
    default_values() {
        Person person1 = new Person();
        person1.setAge(30);

        Knight bean2 = Mapper.from(Person::getAge).to(Knight::setAge)
                .andFrom(Getter.ofNullable(Person::getName).orElse("N/A")).to(Knight::setName)
                .andFrom("I seek the grail").to(Knight::setQuest)
                .creatingWith(Knight::new)
                .apply(person1);

        assertThat(bean2.getName(), equalTo("N/A"));
        assertThat(bean2.getAge(), equalTo(30));
        assertThat(bean2.getQuest(), equalTo("I seek the grail"));
    }

    @Test public void
    mapping_with_builder() {
        Person person1 = new Person();
        person1.setName("Antigone");
        person1.setAge(30);

        Knight bean2 = Mapper.from(Person::getAge).to(Knight.Builder::withAge)
                .andFrom(Person::getName).via(String::toUpperCase).to(Knight.Builder::withName)
                .andFrom("I seek the grail").to(Knight.Builder::withQuest)
                .creatingWith(Knight.Builder::new, Knight.Builder::get)
                .apply(person1);

        assertThat(bean2.getName(), equalTo("ANTIGONE"));
        assertThat(bean2.getAge(), equalTo(30));
        assertThat(bean2.getQuest(), equalTo("I seek the grail"));
    }
}
