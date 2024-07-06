package com.ShoeAppBE.DTO;

import com.ShoeAppBE.models.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DTOUser {

    private String firstName;

    private String lastName;

    private String email;

    private String telephone;

    private String username;

    private String password;

    public DTOUser(){}

    public DTOUser(User u){
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.email = u.getEmail();
        this.telephone = u.getTelephone();
        this.username = u.getUsername();
    }
}
