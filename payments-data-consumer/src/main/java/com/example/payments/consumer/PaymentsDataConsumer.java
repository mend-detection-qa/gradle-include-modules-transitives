package com.example.payments.consumer;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.joda.time.DateTime;

/**
 * Sample consumer using aws-lambda-java-events (brings in joda-time)
 * and delegates to PaymentsDataConsumerLib (brings in commons-codec via secretsmanager).
 *
 * TKA-10120 reproducer: when Mend scans this module via
 * gradle.includeModules=payments-data-consumer, BOTH joda-time AND commons-codec
 * must appear in the dependency tree.
 */
public class PaymentsDataConsumer {

    private final com.example.payments.lib.PaymentsDataConsumerLib lib;

    public PaymentsDataConsumer() {
        this.lib = new com.example.payments.lib.PaymentsDataConsumerLib();
    }

    public void handleEvent(SQSEvent event) {
        DateTime now = DateTime.now();
        System.out.println("Processing event at: " + now);
        lib.process(event.getRecords().size());
    }
}
