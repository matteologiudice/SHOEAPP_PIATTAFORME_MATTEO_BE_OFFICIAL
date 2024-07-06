package com.ShoeAppBE.DTO;

import com.ShoeAppBE.models.cart.Cart;
import com.ShoeAppBE.models.cart.InfoCart;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class DTOCart {

    private Long id;
    private List<DTOInfoCart> product;

    public DTOCart(){}
    public DTOCart(Cart c){
        this.id = c.getId();
        HashMap<Long,DTOInfoCart> set = new HashMap<>();
        this.product = new ArrayList<>();
        for(InfoCart prod : c.getProductsInCart()){
            System.out.println(prod.toString());
            set.put(prod.getId(),new DTOInfoCart(prod));
            System.out.println(set.size());
        }
        this.product = new ArrayList<>(set.values());
    }

}
