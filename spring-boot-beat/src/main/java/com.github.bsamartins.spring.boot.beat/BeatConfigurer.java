package com.github.bsamartins.spring.boot.beat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BeatConfigurer {
    private Duration interval = Duration.ofSeconds(1);
    private List<BeatHandler> handlers = new ArrayList<>();
    private List<BeatSupplier<?>> suppliers = new ArrayList<>();

    public Duration getInterval() {
        return interval;
    }

    public void setInterval(Duration interval) {
        this.interval = interval;
    }

    public void registerHandler(BeatHandler handler) {
        this.handlers.add(handler);
    }

    public void registerSupplier(BeatSupplier<?> supplier) {
        this.suppliers.add(supplier);
    }

    List<BeatHandler> getHandlers() {
        return handlers;
    }
    List<BeatSupplier<?>> getSuppliers() {
        return suppliers;
    }
}
