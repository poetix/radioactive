package com.codepoetics.radioactive;

import java.util.Optional;

public final class Validators {
    public Validators() {
    }


    public static <T> Validator<T> notNull() {
        return (t, errors) -> {
            if (t == null) {
                errors.accept("must not be null");
            }
        };
    }

    public static <T> Validator<T> notNull(Validator<T> and) {
        return Validators.<T>notNull().andIfSo(and);
    }

    public static <T> Validator<Optional<T>> notEmpty() {
        return (t, errors) -> {
            if (!t.isPresent()) {
                errors.accept("must not be empty");
            }
        };
    }

    public static <T> Validator<Optional<T>> notEmpty(Validator<T> and) {
        return Validators.<T>notEmpty().andIfSo((maybeT, errors) -> maybeT.ifPresent(t -> and.accept(t, errors)));
    }

    public static <T> Validator<T> equalTo(T expected) {
        return (t, errors) -> {
            if (!expected.equals(t)) {
                errors.accept(String.format("%s != %s", t, expected));
            }
        };
    }

    public static <T extends Number> Validator<T> greaterThanOrEqualTo(Number expected) {
        return (t, errors) -> {
            if (!(t.doubleValue() >= expected.doubleValue())) {
                errors.accept(String.format("%s < %s", t, expected));
            }
        };
    }

    public static <T extends Number> Validator<T> greaterThan(Number expected) {
        return (t, errors) -> {
            if (!(t.doubleValue() > expected.doubleValue())) {
                errors.accept(String.format("%s <= %s", t, expected));
            }
        };
    }

    public static <T extends Number> Validator<T> lessThanOrEqualTo(Number expected) {
        return (t, errors) -> {
            if (!(t.doubleValue() >= expected.doubleValue())) {
                errors.accept(String.format("%s > %s", t, expected));
            }
        };
    }

    public static <T extends Number> Validator<T> lessThan(Number expected) {
        return (t, errors) -> {
            if (!(t.doubleValue() > expected.doubleValue())) {
                errors.accept(String.format("%s >= %s", t, expected));
            }
        };
    }
}
