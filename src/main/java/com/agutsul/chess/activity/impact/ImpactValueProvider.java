package com.agutsul.chess.activity.impact;

import static java.lang.String.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.Supplier;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.slf4j.Logger;

final class ImpactValueProvider implements Supplier<Integer> {

    private static final Logger LOGGER = getLogger(ImpactValueProvider.class);

    private static final int DEFAULT_VALUE = 0;

    private final ImpactValue value;

    ImpactValueProvider(Supplier<Integer> supplier) {
        this.value = new ImpactValue(supplier);
    }

    @Override
    public Integer get() {
        try {
            return this.value.get();
        } catch (ConcurrentException e) {
            LOGGER.error(format("Loading value failed: %s", getStackTrace(e)));
        }

        return DEFAULT_VALUE;
    }

    private static final class ImpactValue extends LazyInitializer<Integer> {

        private final Supplier<Integer> supplier;

        ImpactValue(Supplier<Integer> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected Integer initialize() {
            return this.supplier.get();
        }
    }
}