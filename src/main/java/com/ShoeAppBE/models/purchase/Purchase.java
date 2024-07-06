package com.ShoeAppBE.models.purchase;

import com.ShoeAppBE.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue
    @Column(name = "id_purchase", nullable = false)
    private Long id;

    @Basic
    @Column(name = "date_purchase")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Basic
    @Column(name = "totalprice")
    private Double totalPrice;

    @Basic
    @Column(name = "city")
    private String city;

    @Basic
    @Column(name = "address")
    private String address;

    @Basic
    @Column(name = "zipcode")
    private long zipcode;

    @Basic
    @Column(name = "houseNumber")
    private String houseNumber;

    @ManyToOne(optional = false)
    @JsonIgnore
    private User user;

    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "purchase")
    @JsonIgnore
    private List<InfoPurchase> producsInPurchase;
}
