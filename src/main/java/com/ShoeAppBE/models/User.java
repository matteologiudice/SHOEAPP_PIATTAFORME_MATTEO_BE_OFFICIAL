package com.ShoeAppBE.models;

import com.ShoeAppBE.models.cart.Cart;
import com.ShoeAppBE.models.purchase.Purchase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    public User(){}

    @Id
    @GeneratedValue
    @Column(name = "user_id",nullable = false)
    private Long id;

    @Basic
    @Column(name = "first_name", nullable = false)
    private String lastName;

    @Basic
    @Column(name = "last_name", nullable = false)
    private String firstName;

    @Basic
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Basic
    @Column(name = "telephone")
    private String telephone;

    @Basic
    @Column(name = "username", nullable = false,unique = true)
    private String username;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, mappedBy = "user")
    @JsonIgnore
    private List<Purchase> purchases;

    @OneToOne(cascade = CascadeType.ALL, optional = false, mappedBy = "owner")
    @JsonIgnore
    private Cart cart;

}
