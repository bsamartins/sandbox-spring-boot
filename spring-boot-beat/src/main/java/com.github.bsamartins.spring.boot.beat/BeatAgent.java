package com.github.bsamartins.spring.boot.beat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BeatAgent implements Runnable, InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeatAgent.class);

    private ScheduledExecutorService taskExecutor;
    private ObjectMapper objectMapper;

    private ScheduledFuture scheduledFuture;
    private Duration interval = Duration.ofSeconds(1);
    private List<BeatHandler> handlers;
    private List<BeatSupplier<?>> suppliers;

    public BeatAgent(ScheduledExecutorService taskExecutor, ObjectMapper objectMapper) {
        this.taskExecutor = taskExecutor;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run() {
        LOGGER.trace("Beat");
        this.suppliers.forEach(s -> {
            Object data = s.get();
            JsonNode node = this.objectMapper.valueToTree(data);
            propagateEvent(new Beat<>(node));
        });
    }

    public void setHandlers(List<BeatHandler> handlers) {
        this.handlers = handlers;
    }

    public void setInterval(@NotNull Duration interval) {
        if(interval == null) {
            throw new IllegalArgumentException("Interval required");
        }
        this.interval = interval;
    }

    @Override
    public void destroy() {
        if(this.scheduledFuture != null) {
            LOGGER.info("Shutting down agent");
            this.scheduledFuture.cancel(false);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if((suppliers != null && !suppliers.isEmpty()) && (handlers != null && !handlers.isEmpty())) {
            LOGGER.info("Starting agent");
            this.scheduledFuture = this.taskExecutor.scheduleAtFixedRate(this, 0, this.interval.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            LOGGER.warn("Agent not started. Not suppliers or handlers registered");
        }

    }

    public void setSuppliers(List<BeatSupplier<?>> suppliers) {
        this.suppliers = suppliers;
    }

    private void propagateEvent(Beat<Object> beat) {
        this.handlers.forEach(h -> h.accept(beat));
    }
}
