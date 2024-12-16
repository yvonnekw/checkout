package com.auction.checkout.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        Long orderId,
        BigDecimal amount,
        String paymentMethod
) {
}
