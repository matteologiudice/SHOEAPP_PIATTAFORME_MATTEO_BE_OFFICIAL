package com.ShoeAppBE.repositories.cart;

import com.ShoeAppBE.models.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findCartByOwnerId(Long id);
}
