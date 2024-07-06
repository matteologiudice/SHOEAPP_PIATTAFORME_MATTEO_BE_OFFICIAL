package com.ShoeAppBE.DTO;

import com.ShoeAppBE.models.cart.InfoCart;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DTOInfoCart {

        private int quantity;
        private String size;
        private DTOProduct product;

        public DTOInfoCart(){}

        public DTOInfoCart(InfoCart infoCart){
            this.quantity = infoCart.getQuantity();
            this.product = new DTOProduct(infoCart.getProduct());
            this.size = infoCart.getSize().toString();
        }
}
