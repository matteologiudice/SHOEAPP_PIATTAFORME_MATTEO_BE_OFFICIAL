package com.ShoeAppBE.models.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "brand")
public class Brand {

    @Id
    @GeneratedValue
    @Column(name = "id_brand", nullable = false)
    private Long id;

    @Basic
    @Column(name = "name_brand", nullable = false, unique = true)
    private String name;

    @Basic
    @Column(name = "urlpic_brand")
    private String urlPic;

    @Basic
    @Column(name = "piva", unique = true, nullable = false)
    private String piva;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER, mappedBy = "brand")
    @JsonIgnore
    private List<Product> products = new LinkedList<>();
}
