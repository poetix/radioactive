radioactive
===========

A Java 8 library for building, validating, mutating and mapping beans.

[![Build Status](https://travis-ci.org/poetix/radioactive.svg?branch=master)](https://travis-ci.org/poetix/radioactive)

In Maven:

```xml
<dependency>
    <groupId>com.codepoetics</groupId>
    <artifactId>radioactive</artifactId>
    <version>0.7</version>
</dependency>
```

An ```Accessor``` is a ```Getter``` paired with a ```Setter```, like so:

```java
public static final Accessor<Person, String> NAME = Accessor.of(Person::getName, Person::setName);
public static final Accessor<Person, Address> ADDRESS = Accessor.of(Person::getAddress, Person::setAddress);
```

You can use them to build new objects:

```java
Person person = Person.BUILDER
    .with(Person.NAME.of("Arthur Putey"),
          Person.ADDRESS.of(Address.BUILDER
            .with(Address.FIRST_LINE, "22 Acacia Avenue",
                  Address.SECOND_LINE, "Sunderland",
                  Address.POSTCODE, "VB6 5UX")))
```

to query and modify existing objects:

```java
assertThat(Person.NAME.get(person), equalTo("Arthur Putey"));

assertThat(Person.ADDRESS.join(Address.SECOND_LINE).get(person),
           equalTo("Sunderland"));
           
Person.NAME.set(person, "Angus Yentob");

assertThat(person.getName(), equalTo("Angus Yentob"));

Person.ADDRESS.join(Address.POSTCODE).set(person, "RA8 81T");

assertThat(person.getAddress().getPostCode(), equalTo("RA8 81T"));
```

to validate objects:

```java
Validator<Person> validator = Validator
        .validating("name", Person.NAME, equalTo("Agnes Phobos"))
        .and("age", Person.AGE, greaterThanOrEqualTo(0));

Valid<Person> validPerson = Valid.validate(new Person("Angus Eros", -2), validator);

assertFalse(validPerson.isValid());
assertFalse(validPerson.get().isPresent());
assertThat(validPerson.validationErrors(), hasItems("name: Angus Eros != Agnes Phobos", "age: -2 < 0"));
```

and to map from type to type:

```java
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
```

If you've seen any of my other libraries for doing similar things, notably [Karg](http://github.com/youdevise/karg) and [Octarine](http://github.com/poetix/octarine), you'll have seen most of this before. Radioactive is a variant specifically for working with old-style "bean" objects with getters and setters, because sometimes you have to and why make it more painful than it needs to be?
