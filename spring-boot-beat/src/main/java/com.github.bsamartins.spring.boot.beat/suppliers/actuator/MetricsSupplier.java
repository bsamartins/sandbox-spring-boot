package com.github.bsamartins.spring.boot.beat.suppliers.actuator;

import com.github.bsamartins.spring.boot.beat.BeatSupplier;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MetricsSupplier implements BeatSupplier<Map<String, Double>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsSupplier.class);

    private MeterRegistry registry;

    public MetricsSupplier(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Map<String, Double> get() {
        Map<String, Double> result = new HashMap<>();

        this.registry.getMeters().forEach(meter -> {
            String key = meter.getId().getName();
            String keySuffix = StreamSupport.stream(meter.getId().getTags().spliterator(), false)
                    .map(tag -> tag.getKey() + ":" + tag.getValue().replaceAll("\\s+", ""))
                    .collect(Collectors.joining("."));

            if(keySuffix != null && !keySuffix.trim().equals("")) {
                key += ".{" + keySuffix + "}";
            }

            double value = StreamSupport.stream(meter.measure().spliterator(), false)
                    .filter(measurement -> measurement.getStatistic() == Statistic.VALUE)
                    .mapToDouble(Measurement::getValue)
                    .sum();
            result.put(key, value);
        });
        LOGGER.trace("Retrieved metrics: {}", result);
        return result;
    }
}
