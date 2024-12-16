package com.auction.checkout.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Long cartId,
        List<OrderLineResponse> items,
        BigDecimal totalPrice
) {}

