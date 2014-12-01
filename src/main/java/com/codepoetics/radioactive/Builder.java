package com.codepoetics.radioactive;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Builder<T> extends Supplier<T> {

    static <T> Builder<T> startingWith(Supplier<T> instantiator) {
        List<Consumer<T>> settings = new LinkedList<>();
        return new Builder<T>() {
            @Override
            public <V> Builder<T> with(Setter<? super T, V> setter, Supplier<V> value) {
                settings.add(target -> setter.set(target, value.get()));
                return this;
            }

            @Override
            public T get() {
                T instance = instantiator.get();
                settings.forEach(setting -> setting.accept(instance));
                return instance;
            }
        };
    }

    default <V> Builder<T> with(Setter<? super T, V> setter, V value) {
        return this.<V>with(setter, () -> value);
    }

    <V> Builder<T> with(Setter<? super T, V> setter, Supplier<V> value);
}
