package com.agutsul.chess.activity.impact;

import static java.lang.String.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.Supplier;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.slf4j.Logger;

final class ImpactValueProvider<V extends Number & Comparable<V>> implements Supplier<V> {

    private static final Logger LOGGER = getLogger(ImpactValueProvider.class);

    private final ImpactValue<V> value;
    private final V defaultValue;

    ImpactValueProvider(Supplier<V> supplier, V defaultValue) {
        this.value = new ImpactValue<>(supplier);
        this.defaultValue = defaultValue;
    }

    @Override
    public V get() {
        try {
            return this.value.get();
        } catch (ConcurrentException e) {
            LOGGER.error(format("Loading value failed: %s", getStackTrace(e)));
        }

        return this.defaultValue;
    }

    private static final class ImpactValue<V extends Number & Comparable<V>>
            extends LazyInitializer<V> {

        private final Supplier<V> supplier;

        ImpactValue(Supplier<V> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected V initialize() {
            return this.supplier.get();
        }
    }
}