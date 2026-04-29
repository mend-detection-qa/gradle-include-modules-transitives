package com.example.payments.lib;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

/**
 * Shared library used by payments-data-consumer.
 *
 * Uses AWS SecretsManager SDK which transitively pulls in commons-codec
 * via the Apache HTTP client dependency chain.
 *
 * TKA-10120: When Mend scans payments-data-consumer with includeModules,
 * it must follow this local project dependency and surface commons-codec.
 */
public class PaymentsDataConsumerLib {

    public void process(int recordCount) {
        System.out.println("Processing " + recordCount + " records via lib");
    }

    public SecretsManagerClient buildClient() {
        return SecretsManagerClient.builder().build();
    }
}
