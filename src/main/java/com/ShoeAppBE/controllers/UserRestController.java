package com.ShoeAppBE.controllers;

import com.ShoeAppBE.DTO.DTOUser;
import com.ShoeAppBE.authentication.Utils;
import com.ShoeAppBE.models.User;
import com.ShoeAppBE.services.user.KeycLoakService;
import com.ShoeAppBE.services.user.UserService;
import com.ShoeAppBE.utility.exception.userException.EmailAlreadyExistsException;
import com.ShoeAppBE.utility.exception.userException.KeycloakErrorRegisterUser;
import com.ShoeAppBE.utility.exception.userException.UserNotFoundException;
import com.ShoeAppBE.utility.exception.userException.UsernameAlreadyExistsException;
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
@RequestMapping("/user")
@CrossOrigin("*")
public class UserRestController {

    @Autowired
    UserService userService;

    @Autowired
    KeycLoakService keycLoakService;

    @GetMapping("/getUsers")
    public ResponseEntity getUsers(){
        List<DTOUser> ris = new LinkedList<>();
        if(userService.getUsers().size() > 0) {
            for (User u : userService.getUsers())
            {
                ris.add(new DTOUser(u));
            }
            return new ResponseEntity(new ResponseMessage("Utente Trovato",ris), HttpStatus.OK);
        }
        return new ResponseEntity("No User", HttpStatus.OK);
    }

    @PostMapping("/addUser")
    public ResponseEntity addUser(@RequestBody @Valid DTOUser user){
        try {
            User u = userService.addUser(user);
            return new ResponseEntity<>(new ResponseMessage("User aggiunto",u),HttpStatus.OK);
        } catch (EmailAlreadyExistsException e) {
            return  new ResponseEntity<>(new ResponseMessage("Email già presente",null),HttpStatus.OK);
        } catch(UsernameAlreadyExistsException e){
            return  new ResponseEntity<>(new ResponseMessage("Username già presente",null),HttpStatus.OK);
        } catch(KeycloakErrorRegisterUser e) {
            return  new ResponseEntity<>(new ResponseMessage("Registrazione fallita",null),HttpStatus.OK);
        }
    }

    @GetMapping("/getInfo")
    public ResponseEntity getUserInfo(){
        try{
            User u = userService.getUserByUsername(Utils.getUsername());
            return new ResponseEntity<>(new ResponseMessage("User trovato",new DTOUser((u))),HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessun Utente");
        }
    }

}
