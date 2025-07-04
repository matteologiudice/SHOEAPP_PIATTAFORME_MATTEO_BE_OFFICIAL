package com.ShoeAppBE.repositories.purchase;

import com.ShoeAppBE.models.User;
import com.ShoeAppBE.models.purchase.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query(
            "SELECT p " +
                    "FROM Purchase p " +
                    "WHERE (p.user = :user) AND " +
                    "((cast(:startDate as timestamp)) IS NULL OR p.date >= :startDate ) AND " +
                    "( (cast(:endDate as timestamp)) IS NULL OR p.date <= :endDate )"
    )
    List<Purchase> advanceSearch(
            @Param("user") User user,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    List<Purchase> getPurchaseByUserIdOrderByDateDesc(Long id);
}
