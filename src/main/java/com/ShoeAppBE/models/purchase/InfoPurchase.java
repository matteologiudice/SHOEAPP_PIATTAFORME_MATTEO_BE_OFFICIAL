package com.ShoeAppBE.models.purchase;

import com.ShoeAppBE.models.product.Product;
import com.ShoeAppBE.models.product.Size;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "products_in_purchase")
public class InfoPurchase {

    @Id
    @GeneratedValue
    @Column(name = "id_info_purchase", nullable = false)
    private Long id;

    @Basic
    @Column(name = "price", nullable = false)
    private Double price;

    @Basic
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_size", nullable = false)
    private Size size;

    @ManyToOne(optional = false)
    private Purchase purchase;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    private Product product;

}
