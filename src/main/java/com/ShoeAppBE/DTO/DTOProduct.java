package com.ShoeAppBE.DTO;

import com.ShoeAppBE.models.product.Product;
import com.ShoeAppBE.models.product.SizeDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class DTOProduct {

    public DTOProduct(){}

    private Long id;

    private String name;

    private String barcode;

    private Double price;

    private String category;

    private String description;

    private String quantityWanted;

    private String type;

    private String brand;

    private String pivaBrand;

    private List<DTOSizeDetail> sizeDetails;

    private String urlImage;

    private String urlBrandImage;

    public DTOProduct(Product p){
        this.id = p.getId();
        this.name = p.getName();
        this.barcode = p.getBarCode();
        this.price = p.getPrice();
        this.category = p.getCategory().getName().toString();
        this.type = p.getType().toString();
        this.description = p.getDescription();
        this.brand = p.getBrand().getName();
        this.pivaBrand = p.getBrand().getPiva();
        this.sizeDetails = new ArrayList<>();
        for(SizeDetail sd : p.getSizeDetails()){
            this.sizeDetails.add(new DTOSizeDetail(sd));
        }
        this.urlImage = p.getUrlPic();
        this.urlBrandImage = p.getBrand().getUrlPic();
    }

}
