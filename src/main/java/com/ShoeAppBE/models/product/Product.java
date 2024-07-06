package com.ShoeAppBE.models.product;

import com.ShoeAppBE.DTO.DTOProduct;
import com.ShoeAppBE.DTO.DTOSizeDetail;
import com.ShoeAppBE.models.cart.InfoCart;
import com.ShoeAppBE.models.purchase.InfoPurchase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue
    @Column(name = "id_product", nullable = false)
    private Long id;

    @Basic
    @Column(name = "name_product", nullable = false)
    private String name;

    @Basic
    @Column(name = "barcode_product", nullable = false, unique = true)
    private String barCode;

    @Basic
    @Column(name = "price_product", nullable = false)
    private Double price;

    @Basic
    @Column(name = "urlpic_product")
    private String urlPic;

    @Basic
    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_colours", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "colour", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Colour> colours;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_size_details", joinColumns = @JoinColumn(name = "product_id"))
    private Set<SizeDetail> sizeDetails;

    @Basic
    @Column(name = "type_product", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JsonIgnore
    @ToString.Exclude
    private Category category;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JsonIgnore
    private Brand brand;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, mappedBy = "product")
    @JsonIgnore
    private List<InfoPurchase> productsInPurchases;

    @OneToMany(fetch = FetchType.EAGER, cascade =  CascadeType.PERSIST, mappedBy = "product")
    @JsonIgnore
    private List<InfoCart> productsInCarts;

    public  Product(){}

    public Product(DTOProduct dtoproduct){
        this.name = dtoproduct.getName().toLowerCase();
        this.barCode = dtoproduct.getBarcode().toUpperCase();
        this.price = dtoproduct.getPrice();
        this.type = Type.valueOf(dtoproduct.getType().toUpperCase());
        this.description = dtoproduct.getDescription();

        this.colours = new ArrayList<>();
        /*
        if (dtoproduct.getColours() != null) {
            for (String colour : dtoproduct.getColours()) {
                this.colours.add(Colour.valueOf(colour.toUpperCase()));
            }
        }
         */

        this.sizeDetails = new HashSet<>();
        if (dtoproduct.getSizeDetails() != null) {
            for (DTOSizeDetail dtoSizeDetail : dtoproduct.getSizeDetails()) {
                this.sizeDetails.add(new SizeDetail(dtoSizeDetail));
            }
        }
    }

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

