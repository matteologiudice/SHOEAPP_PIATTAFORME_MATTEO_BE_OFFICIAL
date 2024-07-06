package com.ShoeAppBE.controllers;

import com.ShoeAppBE.DTO.DTOPurchase;
import com.ShoeAppBE.authentication.Utils;
import com.ShoeAppBE.models.purchase.Purchase;
import com.ShoeAppBE.services.purchase.PurchaseService;
import com.ShoeAppBE.utility.exception.productsException.*;
import com.ShoeAppBE.utility.exception.purchaseException.ErrorPurchaseException;
import com.ShoeAppBE.utility.exception.userException.UserNotFoundException;
import com.ShoeAppBE.utility.other.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/purchase")
@CrossOrigin("*")
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @GetMapping("/allPurchase")
    public ResponseEntity getallPurchases(@RequestParam(name = "username", required = false) String username){
        try {
            List<Purchase> ris = purchaseService.getAllPurchases(Utils.getUsername());
            if(ris.size()>0) return new ResponseEntity<>(new ResponseMessage("Acquisti effettuati: ",ris), HttpStatus.OK);
            return new ResponseEntity<>(new ResponseMessage("Nessun Acqsuito Trovato!",null),HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Utente non esiste!",e);
        }
    }

    @GetMapping("/advancePurchaseSearch")
    public ResponseEntity getPurchasesDataRange(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate)
    {
        try {
            List<Purchase> ris = purchaseService.getPurchases(Utils.getUsername(),startDate,endDate);
            if(ris.size() >0) return new ResponseEntity<>(new ResponseMessage("Acquisti Effettuati",ris), HttpStatus.OK);
            else return new ResponseEntity<>(new ResponseMessage("Nessun Acquisto Effettuato", null), HttpStatus.OK);
        } catch (DateErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Range di Date errato!",e);
        } catch (UserNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Utente non esiste!",e);
        }
    }

    @PostMapping("/newPurchase")
    public ResponseEntity<?> newPurchase(@RequestParam(name = "username", required = false) String username,
                                         @RequestBody @Valid DTOPurchase purchas){
        try {
            Purchase purchase = purchaseService.createPurchase(Utils.getUsername(),purchas);
            return new ResponseEntity<>(new ResponseMessage("Acquisto Effettuato",purchase), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Utente non trovato!",e);
        } catch (CartIsEmptyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Carrello è vuoto",e);
        } catch (ErrorPurchaseException e) {
            return new  ResponseEntity<>(new ResponseMessage("Errore nell'acquisto",e.getProduct()), HttpStatus.BAD_REQUEST);
        } catch (ProductNotPresentInCartException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Prodotto non presente nel carrello!",e);
        } catch (DifferentQuantityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quantità di prodotti differite",e);
        } catch (SizeNotAvailableException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Taglia non disponibile",e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getPurchaseInfo(@PathVariable("id") long id){
        try {
            Purchase p = purchaseService.getPurchasesInfo(id);
            return new ResponseEntity<>(new ResponseMessage("Acquisto Trovato", new DTOPurchase(p)), HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity("Nessun Acquisto Trovato",HttpStatus.BAD_REQUEST);
        }
    }
}
