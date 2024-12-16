package com.auction.checkout.client;

import com.auction.checkout.dto.OrderRequest;
import com.auction.checkout.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OrderServiceClient {
    private final RestTemplate restTemplate;

    @Value("${application.config.order-url}")
    private String orderServiceUrl;

    public OrderResponse createOrder(OrderRequest request) {
        String url = orderServiceUrl + "/create-order";
        return restTemplate.postForObject(url, request, OrderResponse.class);
    }
}

