package com.codepoetics.radioactive;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MaybeAccessor<T, V> extends Accessor<T, Optional<V>> {
    static<T, V> com.codepoetics.radioactive.MaybeAccessor<T, V> wrapping(Function<T, Optional<V>> getter, BiConsumer<T, Optional<V>> setter) {
        return new com.codepoetics.radioactive.MaybeAccessor<T, V>() {
            @Override public Optional<V> apply(T target) {
                return getter.apply(target);
            }

            @Override public void accept(T target, Optional<V> newValue) {
                setter.accept(target, newValue);
            }
        };
    }

    default Accessor<T, V> ensuringPresent(Supplier<V> missingValueSupplier) {
        return Accessor.of(t -> {
            Optional<V> maybeValue = get(t);
            if (!maybeValue.isPresent()) {
                maybeValue = Optional.of(missingValueSupplier.get());
                set(t, maybeValue);
            }
            return maybeValue.orElse(null);
        },
        (t, v) -> set(t, Optional.of(v)));
    }

    default <V2> com.codepoetics.radioactive.MaybeAccessor<T, V2> map(Accessor<V, V2> next, Supplier<V> missingValueSupplier, V2 defaultValue) {
        return wrapping(
                t -> get(t).map(next),
                (t, v2) -> next.set(ensuringPresent(missingValueSupplier).get(t), v2.orElse(defaultValue)));
    }

    default <V2> com.codepoetics.radioactive.MaybeAccessor<T, V2> flatMap(Accessor<V, Optional<V2>> next, Supplier<V> missingValueSupplier) {
        return wrapping(
                t -> get(t).flatMap(next),
                (t, v2) -> next.set(ensuringPresent(missingValueSupplier).get(t), v2));
    }
}
