package com.codepoetics.radioactive;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Setter<T, V> extends BiConsumer<T, V> {

    static <T, V> Setter<T, V> of(BiConsumer<T, V> c) {
        return c::accept;
    }
    static <T, V> MaybeSetter<T, V> ofNullable(BiConsumer<T, V> c) {
        return (t, v) -> c.accept(t, v.orElse(null));
    }
    static <T, V> MaybeSetter<T, V>  ofOptional(BiConsumer<T, Optional<V>> c) {
        return c::accept;
    }

    static <K, V> MaybeSetter<Map<K, V>, V> toMap(K key) {
        return (m, v) -> {
            if (v.isPresent()) {
                v.ifPresent(pv -> m.put(key, pv));
            } else {
                m.remove(key);
            }
        };
    }

    static <T> Setter<List<T>, T> toList(int index) {
        return (l, v) -> l.set(index, v);
    }
    static <T> Setter<T[], T> toArray(int index) {
        return (a, v) -> a[index] = v;
    }

    default void set(T target, V newValue) {
        accept(target, newValue);
    }

    default Consumer<V> on(T target) {
        return v -> set(target, v);
    }

    default Consumer<T> of(V value) {
        return t -> set(t, value);
    }

    default Consumer<T> of(Supplier<V> supplier) {
        return t -> set(t, supplier.get());
    }

}
