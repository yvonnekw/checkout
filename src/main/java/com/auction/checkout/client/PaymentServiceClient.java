package com.auction.checkout.client;

import com.auction.checkout.dto.OrderRequest;
import com.auction.checkout.dto.OrderResponse;
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

    /*
    public PaymentResponse processPayment(String username, String firstName, String lastName, String email, PaymentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Username", username);
        headers.set("X-FirstName", firstName);
        headers.set("X-LastName", lastName);
        headers.set("X-Email", email);

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

        PaymentResponse paymentResponse = restTemplate.postForObject(
                paymentServiceUrl + "/process-payment",
                entity,
                PaymentResponse.class
        );

        // After payment confirmation
        if (paymentResponse != null && paymentResponse.isPaymentSuccessful()) {
            // Check if it's a Buy Now purchase
            if (request.isBuyNow()) {
                // Create the order for Buy Now purchase
                OrderRequest orderRequest = new OrderRequest(
                        request.i,
                        username, // Assumed: Product bought by the logged-in user
                        request.quantity(),
                        true // Indicating it's a Buy Now purchase
                );
                OrderResponse orderResponse = orderServiceClient.createOrder(orderRequest);

                // If the order was created successfully, mark the product as bought
                if (orderResponse != null && orderResponse.getOrderId() != null) {
                    // Mark product as sold and unavailable for Buy Now or bidding
                    productService.markProductAsBought(request.getProductId());

                    // After marking as bought, update the product's availability if needed
                    Product product = productService.findProductById(request.getProductId());
                    if (product.getQuantity() <= 0) {
                        // Make the product unavailable for Buy Now and bidding
                        productService.removeProductFromSelling(request.getProductId());
                    }
                }
            }
        }

        return paymentResponse;
    }

     */
    /*
    public PaymentResponse processPayment(String username, String firstName, String lastName, String email, PaymentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Username", username);
        headers.set("X-FirstName", firstName);
        headers.set("X-LastName", lastName);
        headers.set("X-Email", email);

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(paymentServiceUrl + "/process-payment", entity, PaymentResponse.class);
    }

    */
}
