package com.codepoetics.radioactive;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Valid<T> extends Supplier<Optional<T>> {

    static <T> Valid<T> validate(T target, BiConsumer<T, Consumer<String>> validator) {
        List<String> validationErrors = new LinkedList<>();
        validator.accept(target, validationErrors::add);
        return new Valid<T>() {
            @Override
            public Optional<T> get() {
                return isValid()
                        ? Optional.of(target)
                        : Optional.empty();
            }

            @Override
            public Collection<String> validationErrors() {
                return validationErrors;
            }
        };
    }

    default boolean isValid() {
        return validationErrors().isEmpty();
    }

    Collection<String> validationErrors();

}
