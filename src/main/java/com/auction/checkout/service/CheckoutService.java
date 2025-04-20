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
        String firstName = "Temp name";
        String lastName = "Temp last name";
        String email = "tememal@mail.com";

        PaymentResponse paymentResponse = processPayment(username, firstName, lastName, email, paymentRequest);

        if (paymentResponse.isSuccessful()) {
            OrderResponse orderResponse = orderServiceClient.createOrder(orderRequest);

            for (OrderLineResponse orderLine : orderRequest.items()) {
                Long productId = orderLine.productId();
                Integer quantityOrdered = orderLine.quantity();

                try {
                    productServiceClient.markProductAsBought(productId);

                    ProductResponse productResponse = productServiceClient.findProductById(productId);
                    if (productResponse == null) {
                        log.error("Product not found with ID: {}", productId);
                        continue;
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

                        productServiceClient.updateProduct(productResponse);
                        log.info("Product {} marked as unavailable for sale. Quantity left: {}",
                                productResponse.productName(), productResponse.quantity());
                    } else {
                        log.info("Product {} still available for buy now. Quantity left: {}",
                                productResponse.productName(), productResponse.quantity());
                    }

                } catch (Exception e) {
                    log.error("Error updating product with ID: {}. Exception: {}", productId, e.getMessage());
                }
            }

            log.info("Order processed successfully. Order ID: {}", orderResponse.orderId());
        } else {
            log.error("Payment failed. Order could not be processed.");
            throw new RuntimeException("Payment failed, order cannot be processed.");
        }
    }

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



