package com.ShoeAppBE.services.product;

import com.ShoeAppBE.DTO.DTOInfoCart;
import com.ShoeAppBE.DTO.DTOProduct;
import com.ShoeAppBE.models.User;
import com.ShoeAppBE.models.cart.Cart;
import com.ShoeAppBE.models.cart.InfoCart;
import com.ShoeAppBE.models.product.*;
import com.ShoeAppBE.repositories.UserRepository;
import com.ShoeAppBE.repositories.cart.CartRepository;
import com.ShoeAppBE.repositories.cart.InfoCartRepository;
import com.ShoeAppBE.repositories.product.BrandRepository;
import com.ShoeAppBE.repositories.product.CategoryRepository;
import com.ShoeAppBE.repositories.product.ProductRepository;
import com.ShoeAppBE.utility.exception.productsException.*;
import com.ShoeAppBE.utility.exception.userException.UserNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    InfoCartRepository infoCartRepository;

    @Transactional(readOnly = true)
    public List<Product> getProducts(){return productRepository.findAll();}

    @Transactional(readOnly = true)
    public List<Product> getProduct(){return getProductPage(0,5,"id");}

    @Transactional(readOnly = true, propagation = Propagation.NESTED)
    public List<Product> getProductPage(int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pagedResult = productRepository.findAll(paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new LinkedList<>();
        }
    }

    @Transactional(rollbackFor = {CategoryNotExistsException.class, BarCodeAlreadyExistsException.class, BrandNotExistsException.class})
    public Product addProduct(DTOProduct product) throws CategoryNotExistsException, BarCodeAlreadyExistsException, BrandNotExistsException {
        Type type = Type.valueOf(product.getCategory().toUpperCase());
        //controllo se la categoria non esiste (PRIMA CONTROLLO SE LA CATEGORIA ESISTE E POI ME LA VADO A PRENDERE)
        if(product.getCategory() == null || !categoryRepository.existsByName(type))
            throw new CategoryNotExistsException();
        Category c = categoryRepository.findCategoryByName(type);
        //controllo se il brand non esiste (PRIMA CONTROLLO SE IL BRAND ESISTE E POI ME LO VADO A PRENDERE)
        if(product.getBrand() == null || !brandRepository.existsByName(product.getBrand().toLowerCase()))
            throw new BrandNotExistsException();
        Brand b = brandRepository.findByName(product.getBrand().toLowerCase());
        //controllo se il prodotto esiste già utilizzando il BARCODE e la CATEGORY
        if(product.getBarcode() != null && productRepository.existsProductByBarCodeAndCategory(product.getBarcode().toUpperCase(),c))
            throw new BarCodeAlreadyExistsException();

        //creo il prodotto una volta passati tutti i controlli
        Product p = new Product(product);
        p.setCategory(c);
        p.setBrand(b);
        c.getProducts().add(p);
        b.getProducts().add(p);

        /**
         * Salva l'entità 'p' nel database. Questo metodo è usato per persistere le modifiche apportate all'oggetto,
         * garantendo che qualsiasi aggiornamento o nuova inserzione venga correttamente registrata nel database.
         * Il metodo 'save()' gestisce anche la generazione automatica degli ID per le nuove entità e assicura
         * che le modifiche siano parte di una transazione gestita, mantenendo la consistenza dei dati.
         *
         * @param p l'entità prodotto da salvare.
         * @return il prodotto salvato con lo stato aggiornato dal database, inclusi eventuali nuovi valori di ID generati.
         */
        return productRepository.save(p);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) throws ProductNotFoundException {
        return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByName(String name){
        return productRepository.findProductsByNameContaining(name.toLowerCase());
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryName(String nameCategory) throws CategoryNotExistsException {
        try {
            Type type = Type.valueOf(nameCategory.toUpperCase());
            if (!categoryRepository.existsByName(type)) throw new CategoryNotExistsException();
            Category c = categoryRepository.findCategoryByName(type);
            return c.getProducts();
        }
        catch (IllegalArgumentException e) {
            throw new CategoryNotExistsException();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> getProductByBrand(String name) throws BrandNotExistsException {
        Brand b = brandRepository.findByName(name);
        if(b==null) throw new BrandNotExistsException();
        return b.getProducts();
    }

    /**
     * Aggiunge un prodotto al carrello di un utente specificato. Questo metodo gestisce l'aggiunta del prodotto
     * verificando la disponibilità, aggiornando le quantità e salvaguardando le operazioni tramite una transazione annidata.
     * La transazione è marcata come NESTED per permettere il rollback di questa operazione specifica senza influenzare
     * altre transazioni che potrebbero essere in corso. Il metodo è configurato per eseguire il rollback in caso di
     * eccezioni specifiche come mancata disponibilità del prodotto o quantità non valida.
     *
     * Se la quantità è minore o uguale a zero, viene lanciata un'eccezione per garantire che solo valori validi
     * vengano processati. Il metodo cerca l'utente e il prodotto nel database; se uno dei due non è trovato,
     * viene lanciata l'eccezione corrispondente. Una volta trovati l'utente e il prodotto, si verifica se il prodotto
     * è già presente nel carrello. Se non è presente, viene creato un nuovo record nel carrello, altrimenti
     * la quantità esistente viene aggiornata.
     *
     * @param username il nome utente del cliente a cui aggiungere il prodotto al carrello.
     * @param idProd l'ID del prodotto da aggiungere al carrello.
     * @param quantity la quantità del prodotto da aggiungere; deve essere maggiore di zero.
     * @return l'oggetto Cart aggiornato con il prodotto aggiunto o la quantità aggiornata.
     * @throws UserNotFoundException se l'utente non viene trovato.
     * @throws ProductNotFoundException se il prodotto non viene trovato.
     * @throws QuantityNotAvailableException se la quantità richiesta non è disponibile.
     * @throws QuantityMustBePositiveAndGreaterThanZero se la quantità inserita non è valida.
     * @throws OptimisticLockException se si verifica un conflitto di versionamento durante l'aggiornamento del carrello.
     */
    @Transactional(rollbackFor = {UserNotFoundException.class,ProductNotFoundException.class,QuantityNotAvailableException.class,QuantityMustBePositiveAndGreaterThanZero.class, OptimisticLockException.class},propagation = Propagation.NESTED)
    public Cart addProductCart(String username, Long idProd, int quantity, String size) throws UserNotFoundException, ProductNotFoundException, QuantityNotAvailableException, QuantityMustBePositiveAndGreaterThanZero {
        //Prima di tutto controllo la quantità
        if(quantity <= 0)
            throw new QuantityMustBePositiveAndGreaterThanZero();
        //Trovo l'utente
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException();
        }
        //Trovo il prodotto
        Product product = productRepository.findById(idProd).orElseThrow(ProductNotFoundException::new);
        //Mi vado a prendere il carrello associato all'utente
        Cart cart = cartRepository.findCartByOwnerId(user.getId());
        //Trovo il prodotto nel carrello
        Size taglia = Size.valueOf(size);
        InfoCart productInCart = infoCartRepository.findByProductIdAndCartAndSize(idProd,cart,taglia);

        System.out.println(productInCart);

        if(productInCart == null){
            //Il prodotto non è presente quindi lo creo nuovo
            productInCart = new InfoCart();
            productInCart.setQuantity(quantity);
            productInCart.setSize(taglia);
            productInCart.setCart(cart);
            productInCart.setProduct(product);
        }else{
            //Il prodotto è presente e aggiorno la quantitò
            int updatedQuantity = productInCart.getQuantity() + quantity;
            productInCart.setQuantity(updatedQuantity);
        }

        infoCartRepository.save(productInCart);
        return cartRepository.save(cart);
    }

    @Transactional(rollbackFor = {NoSuchElementException.class, UserNotFoundException.class, NoProductInCartException.class,  OptimisticLockException.class})
    public Cart removeProductCart(String username, Long idProd, String size) throws NoSuchElementException, UserNotFoundException, NoProductInCartException {
        //Trovo l'utente
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UserNotFoundException();
        }
        //Se non c'è quel prodotto lancia NoSuchElementException
        Product product = productRepository.findById(idProd).orElseThrow(NoSuchElementException::new);

        //Trovo il carrello associato all'utente
        Cart cart = cartRepository.findCartByOwnerId(user.getId());

        //Trovo il prodotto nel carrello
        Size taglia = Size.valueOf(size);
        InfoCart producInCart = infoCartRepository.findByProductIdAndCartAndSize(product.getId(),cart,taglia);
        System.out.println(producInCart.toString());

        if(producInCart == null) throw new NoProductInCartException();
        // Rimuove il prodotto dall'elenco dei prodotti nel carrello
        cart.getProductsInCart().remove(producInCart);
        // Rimuove il riferimento del carrello dal prodotto
        product.getProductsInCarts().remove(producInCart);

        infoCartRepository.delete(producInCart);
        return cartRepository.save(cart);
    }

    @Transactional(rollbackFor = {UserNotFoundException.class,ProductNotFoundException.class,QuantityNotAvailableException.class,QuantityMustBePositiveAndGreaterThanZero.class, OptimisticLockException.class})
    public Cart addAProductCart(String username, DTOInfoCart[] products) throws UserNotFoundException, ProductNotFoundException, QuantityNotAvailableException, QuantityMustBePositiveAndGreaterThanZero {
        Cart cart = null;
        //Per ogni prodotto lato FE lo vado ad aggiungere tramite il metodo sopra descritto
        for(DTOInfoCart prod : products){
            System.out.println(prod.toString());
            cart = addProductCart(username,prod.getProduct().getId(), prod.getQuantity(), prod.getSize()); //T.N.
        }
        //Restituisco il carrello associato all'utente aggiornato
        return cart;
    }

    @Transactional(readOnly = true)
    public List<Product> searchAdvancedProduct(String name, String nameCategory, String type, String nameBrand, String priceMin, String priceMax, String size, List<String> colourNames) throws CategoryNotExistsException {
        Category category = null;
        Type productType = null;
        Brand brand = null;
        Double pricemn = null;
        Double pricemx = null;
        Size taglia = null;
        List<Colour> colours = new ArrayList<>();

        if(name != null){
            name = "%" + name + "%";
        }

        if (nameCategory != null) {
            Type typeCat = Type.valueOf(nameCategory.toUpperCase());
            category = categoryRepository.findCategoryByName(typeCat);
            if (category == null) {
                throw new CategoryNotExistsException();
            }
        }

        if (type != null) {
            productType = Type.valueOf(type.toUpperCase());
        }

        if (nameBrand != null) {
            brand = brandRepository.findByName(nameBrand.toLowerCase());
        }

        if (priceMin != null) {
            pricemn = Double.parseDouble(priceMin);
        }

        if (priceMax != null) {
            pricemx = Double.parseDouble(priceMax);
        }

        if (size != null) {
            taglia = Size.valueOf(size.toUpperCase());
        }

        if (colourNames != null && !colourNames.isEmpty()) {
            for (String colourName : colourNames) {
                colours.add(Colour.valueOf(colourName.toUpperCase()));
            }
        }

        return productRepository.advancedSearch(name, category, productType, brand, pricemn, pricemx, taglia);
    }

    @Transactional(readOnly = true)
    public Cart getCartUser(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) throw new UserNotFoundException();
        return cartRepository.findCartByOwnerId(user.getId());
    }
}
