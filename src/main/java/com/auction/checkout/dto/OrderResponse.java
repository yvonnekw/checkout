package com.auction.checkout.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long cartId,
        List<OrderLineRequest> items,
        BigDecimal totalPrice
) {}

