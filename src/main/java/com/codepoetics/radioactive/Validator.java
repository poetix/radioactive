package com.codepoetics.radioactive;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Validator<T> extends BiConsumer<T, Consumer<String>> {
    static <T, V> Validator<T> validating(Function<T, V> extractor, Validator<V> valueValidator) {
        return (t, errors) -> valueValidator.accept(extractor.apply(t), errors);
    }

    static <T, V> Validator<T> validating(String propertyName, Function<T, V> extractor, Validator<V> valueValidator) {
        return (t, errors) -> valueValidator.accept(
                extractor.apply(t),
                error -> errors.accept(String.format("%s: %s", propertyName, error)));
    }

    default Validator<T> and(Validator<T> next) {
        return (t, errors) -> {
            accept(t, errors);
            next.accept(t, errors);
        };
    }

    default Validator<T> andIfSo(Validator<T> next) {
        Validator<T> self = this;
        return new Validator<T>() {
            private boolean canContinue = true;

            @Override
            public void accept(T t, Consumer<String> errors) {
                self.accept(t, error -> {
                    canContinue = false;
                    errors.accept(error);
                });
                if (canContinue) {
                    next.accept(t, errors);
                }
            }
        };
    }

    default <V> Validator<T> and(Function<T, V> extractor, Validator<V> valueValidator) {
        return and(validating(extractor, valueValidator));
    }

    default <V> Validator<T> and(String propertyName, Function<T, V> extractor, Validator<V> valueValidator) {
        return and(validating(propertyName, extractor, valueValidator));
    }
}
