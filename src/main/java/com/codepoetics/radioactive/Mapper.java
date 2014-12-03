package com.codepoetics.radioactive;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Mapper<L, R, V> extends BiConsumer<L, R> {

    interface RightCapture<L, V> {
        <R> Mapper<L, R, V> to(BiConsumer<R, V> right);
        <V2> RightCapture<L, V2> via(Function<? super V, ? extends V2> mapper);
    }

    interface ContinuingRightCapture<L, R, V> {
        Mapper<L, R, V> to(Setter<R, V> right);
        <V2> ContinuingRightCapture<L, R, V2> via(Function<? super V, ? extends V2> mapper);
    }

    static <L, V> RightCapture<L, V> from(Function<L, V> left) {
        return new RightCapture<L, V>() {
            @Override
            public <R> Mapper<L, R, V> to(BiConsumer<R, V> right) {
                return (l, r) -> right.accept(r, left.apply(l));
            }

            @Override
            public <V2> RightCapture<L, V2> via(Function<? super V, ? extends V2> mapper) {
                return Mapper.<L, V2>from((l) -> mapper.apply(left.apply(l)));
            }
        };
    }

    static <L, V> RightCapture<L, V> from(V constant) {
        return from(l -> constant);
    }

    default <V2> ContinuingRightCapture<L, R, V2> andFrom(Function<L, V2> nextLeft) {
        return new ContinuingRightCapture<L, R, V2>() {
            @Override
            public Mapper<L, R, V2> to(Setter<R, V2> nextRight) {
                return (l, r) -> {
                    accept(l, r);
                    nextRight.accept(r, nextLeft.apply(l));
                };
            }

            @Override
            public <V3> ContinuingRightCapture<L, R, V3> via(Function<? super V2, ? extends V3> mapper) {
                return andFrom(l -> mapper.apply(nextLeft.apply(l)));
            }
        };
    }

    default <V2> ContinuingRightCapture<L, R, V2> andFrom(V2 constant) {
        return andFrom(l -> constant);
    }

    default Function<L, R> creatingWith(Supplier<R> initialiser) {
        return l -> {
            R r = initialiser.get();
            accept(l, r);
            return r;
        };
    }

    default <O> Function<L, O> creatingWith(Supplier<R> builderInitialiser, Function<R, O> buildMethod) {
        return l -> {
            R r = builderInitialiser.get();
            accept(l, r);
            return buildMethod.apply(r);
        };
    }

}
