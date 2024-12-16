package com.auction.checkout.service;

import com.auction.checkout.client.CartServiceClient;
import com.auction.checkout.client.OrderServiceClient;
import com.auction.checkout.client.PaymentServiceClient;
import com.auction.checkout.client.ProductServiceClient;
import com.auction.checkout.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CheckoutService {

    private final CartServiceClient cartServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final ProductServiceClient productServiceClient;

    public void processPaymentAndUpdateProduct(String username, PaymentRequest paymentRequest, OrderRequest orderRequest) {
        // Temp user details, replace with actual values if available
        String firstName = "Temp name";
        String lastName = "Temp last name";
        String email = "tememal@mail.com";

        // Process payment first
        PaymentResponse paymentResponse = processPayment(username, firstName, lastName, email, paymentRequest);

        if (paymentResponse.isSuccessful()) {
            // Proceed to create the order
            OrderResponse orderResponse = orderServiceClient.createOrder(orderRequest);

            // Iterate through order lines to mark products as bought and update their availability status
            for (OrderLineResponse orderLine : orderRequest.items()) {
                Long productId = orderLine.productId();
                Integer quantityOrdered = orderLine.quantity();

                // Mark the product as bought
                try {
                    productServiceClient.markProductAsBought(productId);

                    // Retrieve the updated product and check if its quantity is 0
                    ProductResponse productResponse = productServiceClient.findProductById(productId);
                    if (productResponse == null) {
                        log.error("Product not found with ID: {}", productId);
                        continue;  // Skip this product as it cannot be updated if it's not found
                    }

                    if (productResponse.quantity() <= 0) {
                        productResponse = new ProductResponse(
                                productResponse.productId(),
                                productResponse.username(),
                                productResponse.productName(),
                                productResponse.brandName(),
                                productResponse.description(),
                                productResponse.startingPrice(),
                                productResponse.buyNowPrice(),
                                productResponse.colour(),
                                productResponse.productSize(),
                                productResponse.quantity(),
                                false,
                                productResponse.isSold()
                        );

                        // Persist the updated product status
                        productServiceClient.updateProduct(productResponse);
                        log.info("Product {} marked as unavailable for sale. Quantity left: {}",
                                productResponse.productName(), productResponse.quantity());
                    } else {
                        log.info("Product {} still available for buy now. Quantity left: {}",
                                productResponse.productName(), productResponse.quantity());
                    }

                } catch (Exception e) {
                    // Handle any exceptions that occur while marking the product as bought or updating it
                    log.error("Error updating product with ID: {}. Exception: {}", productId, e.getMessage());
                }
            }

            log.info("Order processed successfully. Order ID: {}", orderResponse.orderId());
        } else {
            // If payment failed, log the error and throw an exception
            log.error("Payment failed. Order could not be processed.");
            throw new RuntimeException("Payment failed, order cannot be processed.");
        }
    }



    /*
    // Method to process the payment and update product status
    public void processPaymentAndUpdateProduct(String username, PaymentRequest paymentRequest, OrderRequest orderRequest) {
        // Process payment first - temp user details
        String firstName = "Temp name";
        String lastName = "Temp last name";
        String email = "tememal@mail.com";

        PaymentResponse paymentResponse = processPayment(username, firstName,  lastName, email, paymentRequest);

        if (paymentResponse.isSuccessful()) {
            // Create the order using the order service client
            OrderResponse orderResponse = orderServiceClient.createOrder(orderRequest);

            // Iterate through order lines to mark products as bought and update their status
            for (OrderLineResponse orderLine : orderRequest.items()) {
                Long productId = orderLine.productId();
                Integer quantityOrdered = orderLine.quantity();

                // Mark the product as bought
                productServiceClient.markProductAsBought(productId);

                // Retrieve the updated product and check if its quantity is 0
                ProductResponse productResponse = productServiceClient.findProductById(productId);
                if (productResponse.quantity() <= 0) {
                    // If the quantity is 0, update the product status to not available for sale
                    productResponse.isAvailableForBuyNow = false;
                    productServiceClient.updateProduct(productResponse);  // Persist the updated product status
                }

                log.info("Product {} status updated. Quantity left: {}", productResponse.productName(), productResponse.quantity());
            }

            log.info("Order processed successfully. Order ID: {}", orderResponse.orderId());
        } else {
            log.error("Payment failed. Order could not be processed.");
            throw new RuntimeException("Payment failed, order cannot be processed.");
        }
    }

*/
    //retrieve user details from the gateway
    private PaymentResponse processPayment(String username, String firstName, String lastName, String email, PaymentRequest paymentRequest) {
        return paymentServiceClient.processPayment(username, firstName,  lastName, email, paymentRequest);
    }
    private OrderRequest mapCartToOrder(CartResponse cart) {
        List<OrderLineResponse> items = cart.items().stream()
                .map(item -> new OrderLineResponse(item.productId(), item.quantity(), item.totalPrice()))
                .toList();

        return new OrderRequest(cart.cartId(), items, calculateTotalPrice(items));
    }

    private BigDecimal calculateTotalPrice(List<OrderLineResponse> items) {
        return items.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



}

/*
    public CheckoutResponse processCheckout(String username) throws Exception {
        CartResponse cart = cartServiceClient.getCart(username);

        if (cart == null || cart.items().isEmpty()) {
            throw new RuntimeException("Cart is empty or unavailable");
        }

        OrderRequest orderRequest = mapCartToOrder(cart);
        OrderResponse order = orderServiceClient.createOrder(orderRequest);

        PaymentRequest paymentRequest = new PaymentRequest(order.orderId(), order.totalPrice());
        PaymentResponse payment = paymentServiceClient.processPayment(paymentRequest);

        if (!payment.isSuccess()) {
            throw new RuntimeException("Payment failed: " + payment.getMessage());
        }

        cartServiceClient.clearCart(username);

        return new CheckoutResponse(order.getOrderId(), payment.getPaymentId(), "Checkout successful");
    }
*/


