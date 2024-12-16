package com.auction.checkout.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long productId,
        Integer quantity,
        BigDecimal price
) {}
