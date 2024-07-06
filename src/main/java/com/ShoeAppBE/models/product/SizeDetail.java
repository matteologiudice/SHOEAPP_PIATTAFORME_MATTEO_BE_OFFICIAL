package com.ShoeAppBE.models.product;

import com.ShoeAppBE.DTO.DTOSizeDetail;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class SizeDetail {

    @Column(name = "shoe_size", nullable = false)
    @Enumerated(EnumType.STRING)
    private Size size;

    @Column(name = "quantity_stored", nullable = false)
    private int quantityStored;

    public SizeDetail(DTOSizeDetail dtoSizeDetail) {
        this.size = Size.valueOf(dtoSizeDetail.getSize().toUpperCase());
        this.quantityStored = Integer.parseInt(dtoSizeDetail.getQuantityStored());
    }

    public SizeDetail() {}

    public SizeDetail(SizeDetail other) {
        this.size = other.size;
        this.quantityStored = other.quantityStored;
    }
}
