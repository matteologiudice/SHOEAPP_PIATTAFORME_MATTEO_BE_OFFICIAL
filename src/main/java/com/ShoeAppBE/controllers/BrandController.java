package com.ShoeAppBE.controllers;

import com.ShoeAppBE.DTO.DTOBrand;
import com.ShoeAppBE.models.product.Brand;
import com.ShoeAppBE.services.product.BrandService;
import com.ShoeAppBE.utility.exception.productsException.BrandAlreadyExistsException;
import com.ShoeAppBE.utility.exception.productsException.BrandNotExistsException;
import com.ShoeAppBE.utility.other.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/brand")
@CrossOrigin("*")
public class BrandController {

    @Autowired
    BrandService brandService;

    @GetMapping("/getBrands")
    public ResponseEntity<?> getBrands(){
        List<Brand> ris = brandService.getBrands();
        if(ris.size()>0) return new ResponseEntity(new ResponseMessage("Brand Trovati",ris), HttpStatus.OK);
        return new ResponseEntity("Nessun Brand Trovato", HttpStatus.OK);
    }

    @GetMapping("/getBrand/{name}")
    public ResponseEntity<ResponseMessage> getBrandByName(@PathVariable(name = "name") String name){
        try {
            Brand brand = brandService.getBrandByName(name);
            return new ResponseEntity<>(new ResponseMessage("Brand Trovato",brand),HttpStatus.OK);

        } catch (BrandNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nessun Brand con nome: "+name);
        }
    }

    @PostMapping("/addNewBrand")
    public ResponseEntity<ResponseMessage> addBrand(@RequestBody DTOBrand brand){
        try {
            Brand b = brandService.addBrand(brand);
            return new ResponseEntity<>(new ResponseMessage("Brand Aggiunto",b), HttpStatus.OK);
        } catch (BrandAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Brand esiste già",e);
        }
    }

    @DeleteMapping("/deleteBrandByName/{name}")
    public ResponseEntity<ResponseMessage> deleteBrandByName(@PathVariable String name) {
        try {
            brandService.deleteBrandByName(name);
            return new ResponseEntity<>(new ResponseMessage("Brand Cancellato"), HttpStatus.OK);
        } catch (BrandNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessun Brand con nome: " + name, e);
        }
    }
}
