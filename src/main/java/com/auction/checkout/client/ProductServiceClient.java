package com.auction.checkout.client;

import com.auction.checkout.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    @Value("${application.config.product-url}")
    private String productServiceUrl;

    // Method to mark a product as bought via the ProductService
    public void markProductAsBought(Long productId) {
        String url = productServiceUrl + "/products/{productId}/mark-as-bought";
        restTemplate.postForObject(url, null, Void.class, productId);
    }

    // Method to find a product by its ID
    public ProductResponse findProductById(Long productId) {
        String url = productServiceUrl + "/products/{productId}";
        return restTemplate.getForObject(url, ProductResponse.class, productId);
    }

    // Method to update the product details after checkout
    public void updateProduct(ProductResponse productResponse) {
        String url = productServiceUrl + "/products/{productId}";
        restTemplate.put(url, productResponse, productResponse.productId());
    }
}
