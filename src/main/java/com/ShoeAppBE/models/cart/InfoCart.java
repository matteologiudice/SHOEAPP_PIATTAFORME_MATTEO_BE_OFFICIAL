package com.ShoeAppBE.models.cart;

import com.ShoeAppBE.models.product.Product;
import com.ShoeAppBE.models.product.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "infos_cart")
public class InfoCart
{
    @Id
    @GeneratedValue
    @Column(name = "id_info_Cart", nullable = false)
    @JsonIgnore
    private Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Cart cart;

    @Basic
    @Column(name = "quantity_product_in_cart", nullable = false)
    @JsonIgnore
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_size", nullable = false)
    private Size size;

    @ManyToOne(cascade = {CascadeType.PERSIST}, optional = false)
    private Product product;

    @Version
    @JsonIgnore
    @Column(name = "version", nullable = false)
    private long version;

}
