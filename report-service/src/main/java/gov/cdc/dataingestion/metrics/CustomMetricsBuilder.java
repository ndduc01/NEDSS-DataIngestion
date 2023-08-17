package gov.cdc.dataingestion.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetricsBuilder {
    private final MeterRegistry meterRegistry;

    public static Counter custom_total_messages_processed;
    public static Counter custom_total_messages_validated;
    public static Counter custom_total_validated_success;
    public static Counter custom_total_validated_failure;
    public static Counter custom_total_duplicate_hl7_found;
    public static Counter custom_total_xml_conversion_requested;
    public static Counter custom_total_xml_converted_success;
    public static Counter custom_total_xml_converted_failure;
    public static Counter custom_total_tokens_requested;


    public CustomMetricsBuilder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.custom_total_messages_processed = Counter
                .builder("custom_total_messages_processed")
                .register(meterRegistry);
        this.custom_total_messages_validated = Counter
                .builder("custom_total_messages_validated")
                .register(meterRegistry);
        this.custom_total_validated_success = Counter
                .builder("custom_total_validated_success")
                .register(meterRegistry);
        this.custom_total_validated_failure = Counter
                .builder("custom_total_validated_failure")
                .register(meterRegistry);
        this.custom_total_duplicate_hl7_found = Counter
                .builder("custom_total_duplicate_hl7_found")
                .register(meterRegistry);
        this.custom_total_xml_conversion_requested = Counter
                .builder("custom_total_xml_conversion_requested")
                .register(meterRegistry);
        this.custom_total_xml_converted_success = Counter
                .builder("custom_total_xml_converted_success")
                .register(meterRegistry);
        this.custom_total_xml_converted_failure = Counter
                .builder("custom_total_xml_converted_failure")
                .register(meterRegistry);
        this.custom_total_tokens_requested = Counter
                .builder("custom_total_tokens_requested")
                .register(meterRegistry);
    }
}
