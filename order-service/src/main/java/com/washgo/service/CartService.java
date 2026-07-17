package com.washgo.service;

import com.washgo.dto.request.AddCartItemRequest;
import com.washgo.dto.request.CreateCartRequest;
import com.washgo.dto.response.CartResponse;

public interface CartService {

    CartResponse createCart(CreateCartRequest request);

    CartResponse getCart(Long customerId);

    CartResponse addItem(Long customerId, AddCartItemRequest request);

    void removeItem(Long cartItemId);

    void clearCart(Long customerId);
}