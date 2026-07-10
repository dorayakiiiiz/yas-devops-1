package com.yas.commonlibrary.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for enabling detailed JVM metrics collection.
 * This ensures all microservices expose comprehensive metrics for monitoring.
 */
@Configuration
public class MetricsConfig {

    private final MeterRegistry meterRegistry;

    public MetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Binds JVM and system metrics to the MeterRegistry.
     * These metrics include:
     * - JVM Memory (heap, non-heap)
     * - JVM Garbage Collection
     * - JVM Threads
     * - Class Loader
     * - CPU Processor
     * - System Uptime
     * - File Descriptors
     */
    @PostConstruct
    public void bindMetrics() {
        // JVM Memory metrics: heap, non-heap, buffer pools
        new JvmMemoryMetrics().bindTo(meterRegistry);
        
        // JVM GC metrics: pause duration, count
        new JvmGcMetrics().bindTo(meterRegistry);
        
        // JVM Thread metrics: live threads, daemon threads, peak threads
        new JvmThreadMetrics().bindTo(meterRegistry);
        
        // Class Loader metrics: loaded, unloaded classes
        new ClassLoaderMetrics().bindTo(meterRegistry);
        
        // Processor metrics: CPU usage, available processors
        new ProcessorMetrics().bindTo(meterRegistry);
        
        // Uptime metrics: application uptime
        new UptimeMetrics().bindTo(meterRegistry);
        
        // File descriptor metrics: open files, max files
        new FileDescriptorMetrics().bindTo(meterRegistry);
    }
}
