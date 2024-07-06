package com.ShoeAppBE.repositories.cart;

import com.ShoeAppBE.models.cart.Cart;
import com.ShoeAppBE.models.cart.InfoCart;
import com.ShoeAppBE.models.product.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoCartRepository extends JpaRepository<InfoCart, Long> {

    InfoCart findByProductIdAndCart(Long id, Cart cart);

    InfoCart findByProductIdAndCartAndSize(Long id, Cart cart, String size);

    InfoCart findByProductIdAndCartAndSize(Long id, Cart cart, Size size);
}
