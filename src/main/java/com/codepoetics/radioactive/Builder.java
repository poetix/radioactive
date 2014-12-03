package com.codepoetics.radioactive;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Builder<B, T> extends Supplier<T> {

    static <T> Builder<T, T> startingWith(Supplier<T> instantiator) {
        return buildingWith(instantiator, Function.identity());
    }
    
    static <B, T> Builder<B, T> buildingWith(Supplier<B> builderSupplier, Function<B, T> buildMethod) {
        List<Consumer<? super B>> settings = new LinkedList<>();
        return new Builder<B, T>() {
            @Override
            public Builder<B, T> with(Consumer<? super B> setting) {
                settings.add(setting);
                return this;
            }

            @Override
            public T get() {
                B builder = builderSupplier.get();
                settings.forEach(setting -> setting.accept(builder));
                return buildMethod.apply(builder);
            }
        };
    }

    default <V> Builder<B, T> with(Setter<? super B, V> setter, V value) {
        return with(target -> setter.set(target, value));
    }

    default <V1, V2> Builder<B, T> with(Setter<? super B, V1> setter1, V1 value1,
                                     Setter<? super B, V2> setter2, V2 value2) {
        return with(setter1, value1).with(setter2, value2);
    }
    default <V1, V2, V3> Builder<B, T> with(Setter<? super B, V1> setter1, V1 value1,
                                         Setter<? super B, V2> setter2, V2 value2,
                                         Setter<? super B, V3> setter3, V3 value3) {
        return with(setter1, value1).with(setter2, value2).with(setter3, value3);
    }
    default <V1, V2, V3, V4> Builder<B, T> with(Setter<? super B, V1> setter1, V1 value1,
                                             Setter<? super B, V2> setter2, V2 value2,
                                             Setter<? super B, V3> setter3, V3 value3,
                                             Setter<? super B, V4> setter4, V4 value4) {
        return with(setter1, value1).with(setter2, value2).with(setter3, value3).with(setter4, value4);
    }
    default <V1, V2, V3, V4, V5> Builder<B, T> with(Setter<? super B, V1> setter1, V1 value1,
                                                 Setter<? super B, V2> setter2, V2 value2,
                                                 Setter<? super B, V3> setter3, V3 value3,
                                                 Setter<? super B, V4> setter4, V4 value4,
                                                 Setter<? super B, V5> setter5, V5 value5) {
        return with(setter1, value1).with(setter2, value2).with(setter3, value3).with(setter4, value4).with(setter5, value5);
    }

    default <V> Builder<B, T> with(Setter<? super B, V> setter, Supplier<V> value) {
        return with(target -> setter.set(target, value.get()));
    }

    Builder<B, T> with(Consumer<? super B> setting);
}
