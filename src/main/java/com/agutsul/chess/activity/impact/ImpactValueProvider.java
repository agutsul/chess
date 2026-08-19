package com.agutsul.chess.activity.impact;

import static java.lang.String.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.Supplier;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.slf4j.Logger;

final class ImpactValueProvider<VALUE extends Number & Comparable<VALUE>>
        implements Supplier<VALUE> {

    private static final Logger LOGGER = getLogger(ImpactValueProvider.class);

    private final LazyInitializer<VALUE> valueInitializer;
    private final VALUE defaultValue;

    ImpactValueProvider(Supplier<VALUE> supplier, VALUE defaultValue) {
        this.valueInitializer = new LazyValueInitializer<>(supplier);
        this.defaultValue = defaultValue;
    }

    @Override
    public VALUE get() {
        try {
            return this.valueInitializer.get();
        } catch (ConcurrentException e) {
            LOGGER.error(format("Loading value failed: %s", getStackTrace(e)));
        }

        return this.defaultValue;
    }

    private static final class LazyValueInitializer<VALUE extends Number & Comparable<VALUE>>
            extends LazyInitializer<VALUE> {

        private final Supplier<VALUE> supplier;

        LazyValueInitializer(Supplier<VALUE> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected VALUE initialize() {
            return this.supplier.get();
        }
    }
}