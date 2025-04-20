package com.auction.checkout.client;

import com.auction.checkout.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.auction.checkout.dto.PaymentRequest;

@Service
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${application.config.payment-url}")
    private String paymentServiceUrl;

    @Value("${application.config.order-url}")
    private String orderServiceUrl;

    public PaymentResponse processPayment(String username, String firstName, String lastName, String email, PaymentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Username", username);
        headers.set("X-FirstName", firstName);
        headers.set("X-LastName", lastName);
        headers.set("X-Email", email);

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(paymentServiceUrl + "/process-payment", entity, PaymentResponse.class);
    }
}
