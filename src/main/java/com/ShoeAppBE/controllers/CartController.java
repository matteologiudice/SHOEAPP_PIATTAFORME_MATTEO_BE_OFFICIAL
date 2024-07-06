package com.ShoeAppBE.controllers;

import com.ShoeAppBE.DTO.DTOCart;
import com.ShoeAppBE.DTO.DTOInfoCart;
import com.ShoeAppBE.DTO.DTOProduct;
import com.ShoeAppBE.authentication.Utils;
import com.ShoeAppBE.models.cart.Cart;
import com.ShoeAppBE.services.product.ProductService;
import com.ShoeAppBE.utility.exception.productsException.NoProductInCartException;
import com.ShoeAppBE.utility.exception.productsException.ProductNotFoundException;
import com.ShoeAppBE.utility.exception.productsException.QuantityMustBePositiveAndGreaterThanZero;
import com.ShoeAppBE.utility.exception.productsException.QuantityNotAvailableException;
import com.ShoeAppBE.utility.exception.userException.UserNotFoundException;
import com.ShoeAppBE.utility.other.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cart")
@CrossOrigin("*")
public class CartController {

    @Autowired
    ProductService productService;

    @GetMapping("/getCart")
    public ResponseEntity getCart(@RequestParam(name = "username", required = false) String username){
        try {
            Cart cart = productService.getCartUser(Utils.getUsername());
            System.out.println(Utils.getUsername());
            System.out.println(cart.getProductsInCart().size());
            return new ResponseEntity<>(new ResponseMessage("Carrello Trovato",new DTOCart(cart)), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nessun Utente Trovato",e);
        }
    }

    @PostMapping("/addProductToCart")
    public ResponseEntity addProductToCart(@RequestParam(name = "username", required = false) String username,
                                           @RequestBody @Valid DTOProduct product,
                                           @RequestParam(name = "quantity") int quantity,
                                           @RequestParam(name = "size") String size){
        try{
            if(quantity == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User non esiste!",new QuantityMustBePositiveAndGreaterThanZero());
            Cart cart = productService.addProductCart(Utils.getUsername(),product.getId(),quantity,size);
            return new ResponseEntity<>(new ResponseMessage("Prodotto aggiunto!",new DTOCart(cart)), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User non esiste!",e);
        } catch (QuantityMustBePositiveAndGreaterThanZero e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La quantità deve essere maggiore di 0!",e);
        } catch (ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Il Prodotto non esiste!",e);
        } catch (QuantityNotAvailableException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La quantità richiesta non è disponibile!",e);
        }
    }

    @PostMapping("/addAProductToCart")
    public ResponseEntity addAProductToCart(@RequestBody @Valid DTOInfoCart[] infoCarts,
                                            @RequestParam(name = "username", required = false) String username){
        try{
            System.out.println(infoCarts.length);
            Cart cart = productService.addAProductCart(Utils.getUsername(),infoCarts);
            return new ResponseEntity<>(new ResponseMessage("Prodotti aggiunto!",new DTOCart(cart)), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User non esiste!",e);
        } catch (QuantityMustBePositiveAndGreaterThanZero e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La quantità deve essere maggiore di 0!",e);
        } catch (ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Il Prodotto non esiste!",e);
        } catch (QuantityNotAvailableException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La quantità richiesta non è disponibile!",e);
        }
    }

    @DeleteMapping("/deleteProductFromCart")
    public ResponseEntity removeProductFromCart(@RequestBody @Valid DTOProduct product,
                                                @RequestParam(name = "username", required = false) String username,
                                                @RequestParam(name = "size") String size){
        try {
            System.out.println(product.toString());
            System.out.println(size);
            Cart cart = productService.removeProductCart(Utils.getUsername(),product.getId(),size);
            return new ResponseEntity<>(new ResponseMessage("Prodotto Rimosso!",new DTOCart(cart)), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User non esiste!",e);
        } catch (NoProductInCartException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Il prodotto non è nel carrello!",e);
        }
    }
}
