package com.auction.checkout.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long cartItemId,
        Long productId,
        int quantity,
        BigDecimal totalPrice
) {
}
