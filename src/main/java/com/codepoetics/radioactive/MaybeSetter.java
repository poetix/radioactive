package com.codepoetics.radioactive;

import java.util.Optional;

public interface MaybeSetter<T, V> extends Setter<T, Optional<V>> {
    default Setter<T, V> assertNotNull() {
        return (t, v) -> set(t, Optional.of(v));
    }
}
