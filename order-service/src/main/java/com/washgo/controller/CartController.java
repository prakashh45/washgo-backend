package com.washgo.controller;


import com.washgo.dto.request.AddCartItemRequest;
import com.washgo.dto.request.CreateCartRequest;
import com.washgo.dto.response.CartResponse;
import com.washgo.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse createCart(@Valid @RequestBody CreateCartRequest request) {
        return cartService.createCart(request);
    }

    @GetMapping("/{customerId}")
    public CartResponse getCart(@PathVariable Long customerId) {
        return cartService.getCart(customerId);
    }

    @PostMapping("/{customerId}/items")
    public CartResponse addItem(
            @PathVariable Long customerId,
            @Valid @RequestBody AddCartItemRequest request) {

        return cartService.addItem(customerId, request);
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
    }
}