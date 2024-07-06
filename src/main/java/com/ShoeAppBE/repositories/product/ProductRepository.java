package com.ShoeAppBE.repositories.product;

import com.ShoeAppBE.models.product.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findProductsByNameContaining(String name);

    boolean existsProductByBarCodeAndCategory(String barcode, Category category);

    /*
    @Query(
            "SELECT p " +
                    "FROM Product p " +
                    "JOIN p.sizeDetails sd " +
                    "JOIN p.colours c " +
                    "WHERE " +
                    "(p.name LIKE %:name% OR :name IS NULL) AND"+
                    "(p.category = :category OR :category IS NULL) AND "+
                    "(p.price >= :pricemin OR :pricemin IS NULL)  AND "+
                    "(p.price <= :pricemax OR :pricemax IS NULL) AND "+
                    "(p.type = :type OR :type IS NULL ) AND "+
                    "(p.brand = :brand OR :brand IS NULL ) AND"+
                    "(sd.size IN :sizes OR :sizes IS NULL) AND " +
                    "(c IN :colours OR :colours IS NULL)"

    )
    List<Product> advancedSearch(@Param("name") String name,
                                 @Param("category") Category category,
                                 @Param("type") Type type,
                                 @Param("brand") Brand brand,
                                 @Param("pricemin") Double pricemin,
                                 @Param("pricemax") Double pricemax,
                                 @Param("sizes") Set<Size> sizes,
                                 @Param("colours") List<Colour> colours);

     */

    /*
    @Query(
            "SELECT DISTINCT p FROM Product p " +
                    "JOIN p.sizeDetails sd " +
                    "JOIN p.colours c " +
                    "WHERE " +
                    "(sd.size IN :sizes OR :sizes IS NULL) AND " +
                    "(c IN :colours OR :colours IS NULL)"
    )
    List<Product> findBySizesAndColours(
            @Param("sizes") Set<Size> size,
            @Param("colours") List<Colour> colours);

     */

    @Query("SELECT p " +
            "FROM Product p " +
            "LEFT JOIN p.sizeDetails sd " +
            "WHERE (p.name LIKE %:name% OR :name IS NULL) " +
            "AND (p.category = :category OR :category IS NULL) " +
            "AND (p.price >= :pricemin OR :pricemin IS NULL) " +
            "AND (p.price <= :pricemax OR :pricemax IS NULL) " +
            "AND (p.type = :typee OR :typee IS NULL) " +
            "AND (p.brand = :brand OR :brand IS NULL) " +
            "AND (sd.size = :size OR :size IS NULL)")
    List<Product> advancedSearch(@Param("name") String name,
                                 @Param("category") Category category,
                                 @Param("typee") Type type,
                                 @Param("brand") Brand brand,
                                 @Param("pricemin") Double pricemin,
                                 @Param("pricemax") Double pricemax,
                                 @Param("size") Size size);


}
