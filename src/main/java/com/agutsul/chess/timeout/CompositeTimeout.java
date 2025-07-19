package com.agutsul.chess.timeout;

import static java.util.stream.Collectors.summingLong;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.join;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class CompositeTimeout
        implements Iterable<Timeout>, Timeout {

    private final List<Timeout> timeouts = new ArrayList<>();

    public CompositeTimeout(Timeout timeout, Timeout... timeouts) {
        this(Stream.of(List.of(timeout), List.of(timeouts))
                .flatMap(Collection::stream)
                .toList()
        );
    }

    public CompositeTimeout(List<Timeout> timeouts) {
        var list = timeouts.stream()
                .filter(Objects::nonNull)
                .toList();

        if (isEmpty(list)) {
            throw new IllegalStateException("Unable to create composite timeout");
        }

        this.timeouts.addAll(list);
    }

    @Override
    public Optional<Duration> getDuration() {
        var totalMillis = this.timeouts.stream()
                .filter(timeout -> timeout.isType(Type.GENERIC))
                .map(Timeout::getDuration)
                .flatMap(Optional::stream)
                .collect(summingLong(Duration::toMillis));

        return Optional.ofNullable(totalMillis > 0
                ? Duration.ofMillis(totalMillis)
                : null
        );
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean isType(Type type) {
        return this.timeouts.stream()
                .anyMatch(timeout -> timeout.isType(type));
    }

    @Override
    public boolean isAnyType(Type type, Type... types) {
        return this.timeouts.stream()
                .anyMatch(timeout -> timeout.isAnyType(type, types));
    }

    @Override
    public String toString() {
        return join(this.timeouts, ":");
    }

    @Override
    public Iterator<Timeout> iterator() {
        return this.timeouts.iterator();
    }
}