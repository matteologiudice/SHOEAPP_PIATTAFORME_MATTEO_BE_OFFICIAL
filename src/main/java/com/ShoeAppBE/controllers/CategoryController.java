package com.ShoeAppBE.controllers;

import com.ShoeAppBE.models.product.Category;
import com.ShoeAppBE.services.product.CategoryService;
import com.ShoeAppBE.utility.exception.productsException.CategoryAlreadyExistsException;
import com.ShoeAppBE.utility.exception.productsException.CategoryNotExistsException;
import com.ShoeAppBE.utility.other.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin("*")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/addNewCategory")
    public ResponseEntity<ResponseMessage> addCategory(@RequestParam(name = "nameCategory") String nameCategory)
    {
        try {
            Category c = categoryService.addCategoryByName(nameCategory);
            return new ResponseEntity<>(new ResponseMessage("Categoria Aggiunta",c), HttpStatus.OK);
        } catch (CategoryAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Category already exist!",e);
        } catch (CategoryNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Non è possibile aggiungere questa categoria!",e);
        }
    }

    @GetMapping("/getCategory/{name}")
    public ResponseEntity<ResponseMessage> getCategoryByName(@PathVariable(name = "name") String name){
        try {
            Category c = categoryService.getCategoryByName(name);
            return new ResponseEntity<>(new ResponseMessage("Categoria Trovata",c), HttpStatus.OK);

        } catch (CategoryNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nessuna Categoria con nome: "+name);
        }
    }

    @GetMapping("/getCategories/{id}")
    public ResponseEntity<ResponseMessage> getCategoryById(@PathVariable(name = "id") String id){
        try {
            Category c = categoryService.getCategoryById(Long.parseLong(id));
            return new ResponseEntity<>(new ResponseMessage("Categoria Trovata",c),HttpStatus.OK);

        } catch (CategoryNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nessuna Categoria con ID: "+id);
        }
    }

    @GetMapping("/getCategories")
    public ResponseEntity<ResponseMessage> getCategories(){
        List<Category> ris = categoryService.getAll();
        if(ris.size() != 0) return new ResponseEntity<>(new ResponseMessage("ok",ris),HttpStatus.OK);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nessuna Categoria Disponibile");
    }
}
