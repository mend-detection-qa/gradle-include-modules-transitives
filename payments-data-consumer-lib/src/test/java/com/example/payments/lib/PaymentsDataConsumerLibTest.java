package com.example.payments.lib;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class PaymentsDataConsumerLibTest {

    @Test
    public void testLibInstantiation() {
        PaymentsDataConsumerLib lib = new PaymentsDataConsumerLib();
        assertNotNull(lib);
    }
}
