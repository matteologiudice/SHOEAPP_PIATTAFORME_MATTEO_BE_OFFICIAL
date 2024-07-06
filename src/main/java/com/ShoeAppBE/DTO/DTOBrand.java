package com.ShoeAppBE.DTO;

import com.ShoeAppBE.models.product.Brand;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DTOBrand {

    private String name;
    private String piva;
    private String urlPics;

    public DTOBrand(){}
    public DTOBrand(Brand brand){
        this.piva = brand.getPiva();
        this.name = brand.getPiva();
        this.urlPics = brand.getUrlPic();
    }
}
