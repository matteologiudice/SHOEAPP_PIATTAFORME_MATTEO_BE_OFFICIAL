package com.ShoeAppBE.repositories.product;

import com.ShoeAppBE.models.product.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    boolean existsByName(String name);
    boolean existsByNameAndPiva(String name, String piva);
    Brand findByName(String name);

}
