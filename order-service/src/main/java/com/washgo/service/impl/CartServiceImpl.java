package com.washgo.service.impl;

import com.washgo.dto.request.AddCartItemRequest;
import com.washgo.dto.request.CreateCartRequest;
import com.washgo.dto.response.CartItemResponse;
import com.washgo.dto.response.CartResponse;
import com.washgo.entity.Cart;
import com.washgo.entity.CartItem;
import com.washgo.repository.CartItemRepository;
import com.washgo.repository.CartRepository;
import com.washgo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartResponse createCart(CreateCartRequest request) {

        Cart cart = Cart.builder()
                .customerId(request.getCustomerId())
                .laundryPartnerId(request.getLaundryPartnerId())
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .cartItems(new ArrayList<>())
                .build();

        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    @Override
    public CartResponse getCart(Long customerId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return mapToResponse(cart);
    }

    @Override
    public CartResponse addItem(Long customerId, AddCartItemRequest request) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        BigDecimal totalPrice =
                request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        CartItem item = CartItem.builder()
                .cart(cart)
                .serviceId(request.getServiceId())
                .serviceName(request.getServiceName())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .totalPrice(totalPrice)
                .build();

        cart.getCartItems().add(item);

        cart.setTotalItems(cart.getCartItems().size());

        cart.setTotalAmount(
                cart.getCartItems()
                        .stream()
                        .map(CartItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    @Override
    public void removeItem(Long cartItemId) {

        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public void clearCart(Long customerId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setTotalItems(0);

        cartRepository.save(cart);
    }

    private CartResponse mapToResponse(Cart cart) {

        return CartResponse.builder()
                .id(cart.getId())
                .customerId(cart.getCustomerId())
                .laundryPartnerId(cart.getLaundryPartnerId())
                .totalItems(cart.getTotalItems())
                .totalAmount(cart.getTotalAmount())
                .items(
                        cart.getCartItems()
                                .stream()
                                .map(item -> CartItemResponse.builder()
                                        .id(item.getId())
                                        .serviceId(item.getServiceId())
                                        .serviceName(item.getServiceName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .totalPrice(item.getTotalPrice())
                                        .build())
                                .toList()
                )
                .build();
    }
}