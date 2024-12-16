package com.auction.checkout.dto;

public record CheckoutResponse(
        Long orderId,
        Long paymentId,
        String message
) {}
