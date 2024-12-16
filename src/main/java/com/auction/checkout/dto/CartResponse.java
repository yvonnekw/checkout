package com.auction.checkout.dto;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items
) {

}
