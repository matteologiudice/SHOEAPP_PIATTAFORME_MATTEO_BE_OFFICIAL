package com.ShoeAppBE.DTO;

import com.ShoeAppBE.models.cart.InfoCart;
import com.ShoeAppBE.models.purchase.InfoPurchase;
import com.ShoeAppBE.models.purchase.Purchase;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class DTOPurchase {

    private long id;

    private List<DTOProductInPurchase> product;

    private String city;

    private String address;

    private long zipcode;

    private String houseNumber;

    private double totalPrice;

    public DTOPurchase(){
        this.product = new LinkedList<>();
    }

    public DTOPurchase(Purchase purchase){
        this.id = purchase.getId();
        HashMap<Long,DTOProductInPurchase> set = new HashMap<>();
        this.product = new ArrayList<>();
        for(InfoPurchase p : purchase.getProducsInPurchase()){
            set.put(p.getId(), new DTOProductInPurchase(p));
        }
        this.product = new ArrayList<>(set.values());
        this.totalPrice = purchase.getTotalPrice();
        this.city = purchase.getCity();
        this.address = purchase.getAddress();
        this.zipcode = purchase.getZipcode();
        this.houseNumber = purchase.getHouseNumber();
    }
}
