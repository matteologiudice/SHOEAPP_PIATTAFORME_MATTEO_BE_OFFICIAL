package com.ShoeAppBE.models.cart;

import com.ShoeAppBE.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue
    @Column(name = "id_cart", nullable = false)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @OneToOne
    @JoinColumn(name="owner")
    private User owner;

    @Basic
    @Column(name = "totalPrice", nullable = false)
    private double totalPrice;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "cart")
    private List<InfoCart> productsInCart;

}
