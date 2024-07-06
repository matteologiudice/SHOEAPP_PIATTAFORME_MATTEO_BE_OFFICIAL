package com.ShoeAppBE.models.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@ToString
@Getter
@Setter
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "id_category", nullable = false)
    private Long id;

    @Basic
    @Column(name = "name_category", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Type name;

    @Basic
    @Column(name = "url_category_pics")
    private String urlPics;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "category")
    @JsonIgnore
    private List<Product> products;
}
