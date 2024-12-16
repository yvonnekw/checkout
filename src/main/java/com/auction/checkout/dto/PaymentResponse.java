package com.auction.checkout.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        Long orderId,
        BigDecimal amount,
        boolean isSuccessful
) {}
