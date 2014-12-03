package com.codepoetics.radioactive;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Builder<B, T> implements Supplier<T> {

    static <T> Builder<T, T> startingWith(Supplier<T> instantiator) {
        return buildingWith(instantiator, Function.identity());
    }

    static <T, B extends Supplier<T>> Builder<B, T> buildingWith(Supplier<B> builderSupplier) {
        return buildingWith(builderSupplier, Supplier::get);
    }

    static <B, T> Builder<B, T> buildingWith(Supplier<B> builderSupplier, Function<B, T> buildMethod) {
        return new Builder<B, T>(builderSupplier, buildMethod);
    }

    private final List<Consumer<? super B>> settings = new LinkedList<>();
    private final Supplier<B> builderSupplier;
    private final Function<B, T> buildMethod;

    private Builder(Supplier<B> builderSupplier, Function<B, T> buildMethod) {
        this.builderSupplier = builderSupplier;
        this.buildMethod = buildMethod;
    }

    @SafeVarargs
    public final Builder<B, T> with(Consumer<? super B>...newSettings) {
        settings.addAll(Arrays.asList(newSettings));
        return this;
    }

    @Override
    public T get() {
        B builder = builderSupplier.get();
        settings.forEach(setting -> setting.accept(builder));
        return buildMethod.apply(builder);
    }


    public <V> Builder<B, T> with(Setter<? super B, V> setter, V value) {
        return with(setter.of(value));
    }

    public <V> Builder<B, T> with(Setter<? super B, V> setter, Supplier<V> value) {
        return with(setter.of(value));
    }

}
