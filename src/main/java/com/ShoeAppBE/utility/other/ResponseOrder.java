package com.ShoeAppBE.utility.other;

import com.ShoeAppBE.DTO.DTOProductInPurchase;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseOrder {

    private DTOProductInPurchase product;
    private String message;

    public ResponseOrder(){}

    public ResponseOrder(DTOProductInPurchase product, int errorType){
        this.message = errorType==0 ? "Quantità del prodotto non disponibile!" : "Il prezzo del prodotto è cambiato!";
        this.product = product;
    }

}
