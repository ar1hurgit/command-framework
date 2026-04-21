package me.ar1hurgit.commandframework.framework.core;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

public final class CommandMetrics {

    private final ConcurrentMap<String, MutableMetric> metrics = new ConcurrentHashMap<>();

    public void recordSuccess(String commandKey, Duration duration) {
        metrics.computeIfAbsent(commandKey, ignored -> new MutableMetric()).record(true, duration);
    }

    public void recordFailure(String commandKey, Duration duration) {
        metrics.computeIfAbsent(commandKey, ignored -> new MutableMetric()).record(false, duration);
    }

    public Map<String, Snapshot> snapshot() {
        return metrics.entrySet().stream()
            .collect(java.util.stream.Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> entry.getValue().snapshot()));
    }

    public record Snapshot(long executions, long successes, long failures, Duration averageDuration) {
    }

    private static final class MutableMetric {

        private final LongAdder executions = new LongAdder();
        private final LongAdder successes = new LongAdder();
        private final LongAdder failures = new LongAdder();
        private final LongAdder totalNanos = new LongAdder();

        private void record(boolean success, Duration duration) {
            executions.increment();
            totalNanos.add(duration.toNanos());
            if (success) {
                successes.increment();
                return;
            }
            failures.increment();
        }

        private Snapshot snapshot() {
            long executionCount = executions.longValue();
            long averageNanos = executionCount == 0 ? 0L : totalNanos.longValue() / executionCount;
            return new Snapshot(
                executionCount,
                successes.longValue(),
                failures.longValue(),
                Duration.ofNanos(averageNanos)
            );
        }
    }
}
