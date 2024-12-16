package com.auction.checkout.dto;

import java.math.BigDecimal;

public record OrderLineRequest(
        Long productId,
        Integer quantity,
        BigDecimal price
) {}
