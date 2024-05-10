package com.turnosmedicos.turnosmedicos.services;

import com.turnosmedicos.turnosmedicos.repositories.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShiftService {
    private final AuthService authService;
    private final ShiftRepository shiftRepository;
    //private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, AuthService authService, UserRepository userRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.authService = authService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<Cart> getAllProductsInCart(String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            return cartRepository.findByUserId(userId);
        } throw new UnauthorizedException("Unauthorized: Invalid token");
    }

    public Cart addProductInCart(Long productId, String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The user is not found"));
            Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("The product is not found"));

            Cart existingCart = cartRepository.findByUserAndProduct(user, product);

            if(existingCart==null) {
                Cart cart = new Cart();
                cart.setQuantity(1);
                cart.setUser(user);
                cart.setProduct(product);
                return cartRepository.save(cart);
            } throw new ProductAlreadyInCartException("Product already in cart");
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

    public Boolean deleteProductInCart(Long id, String token) {
        if(authService.validationToken(token)) {
            Cart cart = cartRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("The product with this id: " + id + "is not found"));;
            cartRepository.delete(cart);
            return true;
        } else {
            throw new UnauthorizedException("Unauthorized: invalid token");
        }
    }
}
