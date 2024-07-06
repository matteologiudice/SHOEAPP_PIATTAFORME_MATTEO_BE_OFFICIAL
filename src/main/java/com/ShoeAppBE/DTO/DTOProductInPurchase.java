package com.ShoeAppBE.DTO;

import com.ShoeAppBE.models.purchase.InfoPurchase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DTOProductInPurchase {

    private Integer quantity;
    private String size;
    private Double price;
    private DTOProduct product;

    DTOProductInPurchase(){}

    DTOProductInPurchase(InfoPurchase p ){
        this.quantity = p.getQuantity();
        this.price = p.getPrice();
        this.product = new DTOProduct(p.getProduct());
        this.size = p.getSize().toString();
    }
}
