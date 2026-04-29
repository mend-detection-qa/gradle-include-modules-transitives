package com.example.payments.api;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PaymentsApiTest {

    @Test
    public void testStatus() {
        assertEquals("ok", new PaymentsApi().status());
    }
}
