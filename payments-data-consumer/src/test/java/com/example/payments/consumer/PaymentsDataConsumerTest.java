package com.example.payments.consumer;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class PaymentsDataConsumerTest {

    @Test
    public void testConsumerInstantiation() {
        PaymentsDataConsumer consumer = new PaymentsDataConsumer();
        assertNotNull(consumer);
    }
}
