package com.ShoeAppBE.repositories.product;

import com.ShoeAppBE.models.product.Category;
import com.ShoeAppBE.models.product.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByName(String name);

    Category findCategoryByName(String name);

    boolean existsByName(Type name);

    Category findCategoryByName(Type name);
}
