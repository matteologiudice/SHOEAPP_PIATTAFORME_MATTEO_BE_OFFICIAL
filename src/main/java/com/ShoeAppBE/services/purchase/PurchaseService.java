package com.ShoeAppBE.services.purchase;

import com.ShoeAppBE.DTO.DTOProductInPurchase;
import com.ShoeAppBE.DTO.DTOPurchase;
import com.ShoeAppBE.models.User;
import com.ShoeAppBE.models.cart.Cart;
import com.ShoeAppBE.models.cart.InfoCart;
import com.ShoeAppBE.models.product.Product;
import com.ShoeAppBE.models.product.Size;
import com.ShoeAppBE.models.product.SizeDetail;
import com.ShoeAppBE.models.purchase.InfoPurchase;
import com.ShoeAppBE.models.purchase.Purchase;
import com.ShoeAppBE.repositories.UserRepository;
import com.ShoeAppBE.repositories.cart.CartRepository;
import com.ShoeAppBE.repositories.cart.InfoCartRepository;
import com.ShoeAppBE.repositories.product.ProductRepository;
import com.ShoeAppBE.repositories.purchase.InfoPurchaseRepository;
import com.ShoeAppBE.repositories.purchase.PurchaseRepository;
import com.ShoeAppBE.utility.exception.productsException.*;
import com.ShoeAppBE.utility.exception.purchaseException.ErrorPurchaseException;
import com.ShoeAppBE.utility.exception.userException.UserNotFoundException;
import com.ShoeAppBE.utility.other.ResponseOrder;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PurchaseService {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InfoPurchaseRepository infoPurchaseRepository;

    @Autowired
    InfoCartRepository infoCartRepository;

    @Autowired
    CartRepository cartRepository;

    @Transactional(rollbackFor = {UserNotFoundException.class, CartIsEmptyException.class,ErrorPurchaseException.class,ProductNotPresentInCartException.class,DifferentQuantityException.class,DifferentSizeException.class,SizeNotAvailableException.class, OptimisticLockException.class})
    public Purchase createPurchase(String username, DTOPurchase purchase) throws UserNotFoundException, CartIsEmptyException, ProductNotPresentInCartException, DifferentQuantityException, DifferentSizeException, SizeNotAvailableException, ErrorPurchaseException {

        //Lista dei prodotti nel carrello lato FE
        List<DTOProductInPurchase> productsCart = purchase.getProduct();

        //Utente del carrello
        User user = userRepository.findByUsername(username);
        if(user == null) throw new UserNotFoundException();
        if (productsCart.size() == 0) throw new CartIsEmptyException();
        Cart cart = cartRepository.findCartByOwnerId(user.getId());

        //Creo il nuovo acquisto
        Purchase p = new Purchase();
        p.setUser(user);
        p.setDate(new Date());
        p.setProducsInPurchase(new LinkedList<>());
        p.setCity(purchase.getCity());
        p.setAddress(purchase.getAddress());
        p.setHouseNumber(purchase.getHouseNumber());
        p.setZipcode(purchase.getZipcode());
        user.getPurchases().add(p);
        //Lo rendo managed
        purchaseRepository.save(p);
        //Qui vado a salvare le eventuali modifiche se presenti e verrà restituito se c'è un errore
        List<ResponseOrder> errore = new LinkedList<>();

        //Totale dell'acquisto
        double totalPrice = 0;

        //Per ogni prodotto lato FE
        for(DTOProductInPurchase productInCart : productsCart){

            //Mi trovo il prodotto lato BE
            Product product = productRepository.findById(productInCart.getProduct().getId()).orElseThrow(NoSuchElementException::new);

            //Trovo il prodotto nel carrello
            Size size = Size.valueOf(productInCart.getSize());
            InfoCart infoCart = infoCartRepository.findByProductIdAndCartAndSize(product.getId(),cart,size);

            if(!cart.getProductsInCart().contains(infoCart))
                throw new ProductNotPresentInCartException();

            if(!Objects.equals(productInCart.getPrice(), product.getPrice()))
                errore.add(new ResponseOrder(productInCart,1));

            if(!productInCart.getQuantity().equals(infoCart.getQuantity()))
                throw new DifferentQuantityException();

            SizeDetail sizeDetail = null;
            for (SizeDetail sd : product.getSizeDetails()) {
                if (sd.getSize().toString().equals(productInCart.getSize())) {
                    sizeDetail = sd;
                    break;
                }
            }

            if (sizeDetail == null) {
                throw new SizeNotAvailableException();
            }

            if (sizeDetail.getQuantityStored() < productInCart.getQuantity()) {
                errore.add(new ResponseOrder(productInCart, 0));
            }

            InfoPurchase info = new InfoPurchase();
            info.setProduct(product);
            info.setPrice(productInCart.getPrice());
            info.setQuantity(productInCart.getQuantity());
            info.setSize(Size.valueOf(productInCart.getSize()));
            info.setPurchase(p);
            p.getProducsInPurchase().add(info);

            cart.getProductsInCart().remove(infoCart);
            product.getProductsInCarts().remove(infoCart);
            product.getProductsInPurchases().add(info);

            infoCartRepository.delete(infoCart);

            //per effetti collaterali dell'utilizzo dell'annotation
            //@Embeddable in SizeDetail
            HashSet<SizeDetail> updatedDetails = new HashSet<>();
            for(SizeDetail sd : product.getSizeDetails()) {
                if(sd.getSize().toString().equals(productInCart.getSize())) {
                    SizeDetail update = new SizeDetail(sd);
                    update.setQuantityStored(sizeDetail.getQuantityStored() - info.getQuantity());
                    updatedDetails.add(update);
                }
                else {
                    updatedDetails.add(sd);
                }
            }

            product.setSizeDetails(updatedDetails);
            totalPrice += info.getPrice() * info.getQuantity();
        }
        if(errore.size() > 0){
            //C'è stato qualche errore -> rollback e visualizzazione errore lato FE
            throw new ErrorPurchaseException(errore);
        }
        p.setTotalPrice(totalPrice);
        return  purchaseRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Purchase> getPurchases(String username, Date start, Date end) throws DateErrorException, UserNotFoundException {
        Date dS = null; Date dE = null;
        if(end == null){
            //System.out.println(end);
            dE = new Date();
            //System.out.println(dE);
        }
        if(end!= null){
            dE = end;
        }
        if(start!= null){
            dS = start;
        }
        if(start == null){
            dS = new Date();
        }
        User u = userRepository.findByUsername(username);
        if(u == null) throw new UserNotFoundException();
        return purchaseRepository.advanceSearch(u, dS,dE);
    }

    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) throw new UserNotFoundException();
        return purchaseRepository.getPurchaseByUserIdOrderByDateDesc(user.getId());
    }

    @Transactional(readOnly = true)
    public Purchase getPurchasesInfo(Long id) throws ProductNotFoundException {
        return purchaseRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    }
}
