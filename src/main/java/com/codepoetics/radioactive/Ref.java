package com.codepoetics.radioactive;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface Ref<V> extends Supplier<V>, Consumer<V> {

    static <V> Ref<V> to(V value) {
        return new Ref<V>() {
            private V innerValue = value;
            @Override
            public void accept(V v) {
                innerValue = value;
            }

            @Override
            public V get() {
                return innerValue;
            }
        };
    }

    default void set(V value) {
        accept(value);
    }

    default V updateAndGet(UnaryOperator<V> updater) {
        V result = updater.apply(get());
        set(result);
        return result;
    }

    default V getAndUpdate(UnaryOperator<V> updater) {
        V result = get();
        set(updater.apply(result));
        return result;
    }
}
