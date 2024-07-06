package com.ShoeAppBE.DTO;


import com.ShoeAppBE.models.product.SizeDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DTOSizeDetail {

    private String size;

    private String quantityStored;

    public DTOSizeDetail(SizeDetail sizeDetail) {
        this.size = String.valueOf(sizeDetail.getSize());
        this.quantityStored = String.valueOf(sizeDetail.getQuantityStored());
    }

    public DTOSizeDetail() {}
}
