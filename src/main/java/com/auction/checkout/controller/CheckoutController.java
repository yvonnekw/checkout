package com.auction.checkout.controller;

import com.auction.checkout.client.PaymentServiceClient;
import com.auction.checkout.dto.PaymentRequest;
import com.auction.checkout.dto.PaymentResponse;
import com.auction.checkout.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final PaymentServiceClient paymentServiceClient;

    @PostMapping("/process-payment")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-FirstName") String firstName,
            @RequestHeader("X-LastName") String lastName,
            @RequestHeader("X-Email") String email,
            @RequestBody PaymentRequest paymentRequest) {

        try {
            PaymentResponse response = paymentServiceClient.processPayment(username, firstName, lastName, email, paymentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

