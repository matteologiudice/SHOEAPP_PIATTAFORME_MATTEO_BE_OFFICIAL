package com.ShoeAppBE.services.product;

import com.ShoeAppBE.DTO.DTOBrand;
import com.ShoeAppBE.models.product.Brand;
import com.ShoeAppBE.repositories.product.BrandRepository;
import com.ShoeAppBE.utility.exception.productsException.BrandAlreadyExistsException;
import com.ShoeAppBE.utility.exception.productsException.BrandNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public List<Brand> getBrands(){
        return brandRepository.findAll();
    }

    /**
     * Aggiunge un nuovo brand al database controllando prima che non esista già un brand con lo stesso nome e partita IVA.
     * Utilizza transazioni per garantire l'integrità dei dati, facendo rollback in caso di tentativo di inserimento di un brand già esistente.
     * @param brand
     * @return
     * @throws BrandAlreadyExistsException
     */
    @Transactional(rollbackFor = BrandAlreadyExistsException.class)
    public Brand addBrand(DTOBrand brand) throws BrandAlreadyExistsException {
        if(brandRepository.existsByNameAndPiva(brand.getName().toLowerCase(),brand.getPiva().toUpperCase())) throw new BrandAlreadyExistsException();
        Brand b = new Brand();
        b.setName(brand.getName().toLowerCase());
        b.setPiva(brand.getPiva().toUpperCase());
        return brandRepository.save(b);
    }

    @Transactional(readOnly = true)
    public Brand getBrandByName(String name) throws  BrandNotExistsException {
        Brand brand = brandRepository.findByName(name);
        if(brand!= null){
            return brand;
        }
        else {
            throw new BrandNotExistsException();
        }
    }

    @Transactional(rollbackFor = BrandAlreadyExistsException.class)
    public void deleteBrandByName(String name) throws BrandNotExistsException {
        Brand brand = brandRepository.findByName(name.toLowerCase());
        if (brand != null) {
            brandRepository.delete(brand);
        } else {
            throw new BrandNotExistsException();
        }
    }
}
