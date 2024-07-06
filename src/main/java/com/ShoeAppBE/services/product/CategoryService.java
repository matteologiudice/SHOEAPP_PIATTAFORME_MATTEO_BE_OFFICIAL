package com.ShoeAppBE.services.product;

import com.ShoeAppBE.models.product.Category;
import com.ShoeAppBE.models.product.Type;
import com.ShoeAppBE.repositories.product.CategoryRepository;
import com.ShoeAppBE.utility.exception.productsException.CategoryAlreadyExistsException;
import com.ShoeAppBE.utility.exception.productsException.CategoryNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAll(){return categoryRepository.findAll();}

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) throws CategoryNotExistsException {return categoryRepository.findById(id).orElseThrow(CategoryNotExistsException::new);}

    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) throws CategoryNotExistsException {
        try {
            Type type = Type.valueOf(name.toUpperCase());
            if (categoryRepository.existsByName(type)) {
                return categoryRepository.findCategoryByName(type);
            } else {
                throw new CategoryNotExistsException();
            }
        } catch (IllegalArgumentException e) {
            throw new CategoryNotExistsException();
        }
    }

    @Transactional(rollbackFor = {CategoryAlreadyExistsException.class})
    public Category addCategoryByName(String name) throws CategoryAlreadyExistsException, CategoryNotExistsException {
        try {
            Type type = Type.valueOf(name.toUpperCase());
            if (categoryRepository.existsByName(type)) {
                throw new CategoryAlreadyExistsException();
            }
            Category category = new Category();
            category.setName(type);
            return categoryRepository.save(category);
        } catch (IllegalArgumentException e) {
            throw new CategoryNotExistsException();
        }
    }

}
