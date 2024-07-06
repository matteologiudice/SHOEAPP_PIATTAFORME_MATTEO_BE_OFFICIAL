package com.ShoeAppBE.controllers;

import com.ShoeAppBE.DTO.DTOProduct;
import com.ShoeAppBE.models.product.Product;
import com.ShoeAppBE.services.product.BrandService;
import com.ShoeAppBE.services.product.CategoryService;
import com.ShoeAppBE.services.product.ProductService;
import com.ShoeAppBE.utility.exception.productsException.BarCodeAlreadyExistsException;
import com.ShoeAppBE.utility.exception.productsException.BrandNotExistsException;
import com.ShoeAppBE.utility.exception.productsException.CategoryNotExistsException;
import com.ShoeAppBE.utility.exception.productsException.ProductNotFoundException;
import com.ShoeAppBE.utility.other.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @GetMapping("/getSizeProducts")
    public ResponseEntity<?> getSizeProducts(){
        return new ResponseEntity<>(new ResponseMessage("totali prodotti",productService.getProducts().size()), HttpStatus.OK);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity getProducts(){
        List<Product> ris = productService.getProducts();

        if(ris.size() != 0) {
            LinkedList<DTOProduct> result = new LinkedList<>();
            for(Product product : ris){
                result.add(new DTOProduct(product));
            }
            return new ResponseEntity<>(new ResponseMessage("prodotti trovati",result), HttpStatus.OK);
        }

        return new ResponseEntity(new ResponseMessage("Nessun Prodotto"),HttpStatus.OK);
    }

    @GetMapping("/getAllProductsPage")
    public ResponseEntity getProductPageable(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                             @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Product> ris = productService.getProductPage(pageNumber,pageSize,sortBy);
        if(ris.size()!=0) {
            LinkedList<DTOProduct> result = new LinkedList<>();
            for(Product product : ris){
                result.add(new DTOProduct(product));
            }
            System.out.println(ris.size());
            return new ResponseEntity<>(new ResponseMessage("prodotti trovati",result),HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseMessage("Nessun Prodotto"),HttpStatus.OK);
    }

    @GetMapping("/getProduct/{id}")
    public ResponseEntity getProduct(@PathVariable("id") String id){
        try {
            Product p = productService.getProductById(Long.parseLong(id));
            return new ResponseEntity(new ResponseMessage("Prodotto trovato",new DTOProduct(p)),HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nessun prodotto trovato con ID: "+id);
        }
    }

    @GetMapping("/getProductsByName")
    public ResponseEntity<ResponseMessage> getProductsByName( @RequestParam(name = "name") String name)
    {
        List<DTOProduct> ris = new LinkedList<>();
        List<Product> prod = productService.getProductsByName(name.toLowerCase());
        if(prod.size() > 0){
            for(Product p : prod){
                ris.add(new DTOProduct(p));
            }
            return new ResponseEntity<>(new ResponseMessage("Prodotti trovati!",ris),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage("Nessun Prodotto",ris),HttpStatus.OK);
    }

    @GetMapping("/getCategoryProducts")
    public ResponseEntity<ResponseMessage> getCategoryProducts( @RequestParam(name = "categoryName") String name) throws CategoryNotExistsException
    {
        List<DTOProduct> ris = new LinkedList<>();
        List<Product> prod = productService.getProductsByCategoryName(name.toLowerCase());
        if(prod.size() > 0){
            for(Product p : prod){
                ris.add(new DTOProduct(p));
            }
            return new ResponseEntity<>(new ResponseMessage("ok",ris),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage("Nessun Prodotto",null),HttpStatus.OK);
    }

    @GetMapping("/getBrandProduct")
    public ResponseEntity<ResponseMessage> getBrandProduct(@RequestParam(name = "brandName") String name) throws BrandNotExistsException {
        List<Product> ris = productService.getProductByBrand(name);
        List<DTOProduct> result = new LinkedList<>();
        if(ris.size()>0){
            for(Product p : ris){
                result.add(new DTOProduct(p));
            }
            return new ResponseEntity<>(new ResponseMessage("ok",result),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage("Nessun Prodotto",null),HttpStatus.OK);
    }

    @PostMapping("/addNewProduct")
    public ResponseEntity<ResponseMessage> addProduct(@RequestBody @Valid DTOProduct product)
    {
        try{
            Product p = productService.addProduct(product);
            return new ResponseEntity<>(new ResponseMessage("Prodotto Aggiunto",new DTOProduct(p)),HttpStatus.OK);
        } catch (CategoryNotExistsException e) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria Non Esiste",e);
        } catch (BrandNotExistsException e) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand Non Esiste",e);
        } catch (BarCodeAlreadyExistsException e) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prodotto Esiste Già",e);
        }
    }

    @GetMapping("/getAdvancedSearch")
    public ResponseEntity<ResponseMessage> getAdvancedSearch(
            @RequestParam(name = "nameProduct",required = false) String name,
            @RequestParam(name = "nameCategory", required = false) String nameCategory ,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "nameBrand", required = false) String nameBrand,
            @RequestParam(name = "priceMin", required = false) String priceMin,
            @RequestParam(name = "priceMax",required = false) String priceMax,
            @RequestParam(name = "size", required = false) String size,
            @RequestParam(name = "colours", required = false) List<String> colours)
            throws CategoryNotExistsException
    {
        List<DTOProduct> ris = new LinkedList<>();
        List<Product> prod = productService.searchAdvancedProduct(name,
                nameCategory,type,nameBrand,priceMin,priceMax,size,colours);
        if(prod.size() > 0){
            for(Product p : prod){
                ris.add(new DTOProduct(p));
            }
            return new ResponseEntity<>(new ResponseMessage("ok",ris),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResponseMessage("Nessun Prodotto",null),HttpStatus.OK);
        }
    }

}
