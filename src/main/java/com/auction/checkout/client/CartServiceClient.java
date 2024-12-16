package com.auction.checkout.client;

import com.auction.checkout.dto.CartResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CartServiceClient {

    private final RestTemplate restTemplate;

    @Value("${application.config.cart-url}")
    private String cartServiceUrl;

    public CartResponse getCart(String username) {
        String url = UriComponentsBuilder.fromHttpUrl(cartServiceUrl + "/get-user-cart")
                .queryParam("username", username)
                .toUriString();

        return restTemplate.getForObject(url, CartResponse.class);
    }

    public void clearCart(String username) {
        String url = UriComponentsBuilder.fromHttpUrl(cartServiceUrl + "/clear-cart")
                .queryParam("username", username)
                .toUriString();

        restTemplate.exchange(url, HttpMethod.POST, null, Void.class);
    }
}
