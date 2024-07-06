package com.ShoeAppBE.services.user;

import com.ShoeAppBE.DTO.DTOUser;
import com.ShoeAppBE.models.User;
import com.ShoeAppBE.models.cart.Cart;
import com.ShoeAppBE.repositories.UserRepository;
import com.ShoeAppBE.utility.exception.userException.EmailAlreadyExistsException;
import com.ShoeAppBE.utility.exception.userException.KeycloakErrorRegisterUser;
import com.ShoeAppBE.utility.exception.userException.UserNotFoundException;
import com.ShoeAppBE.utility.exception.userException.UsernameAlreadyExistsException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    String usernameAdmin="matteadmin";

    String passwordAdmin="matteo";
    String role = "customer";

    String serverUrl =  "http://localhost:8080";

    String realm = "provaFinaleAngular";

    String clientId = "angular";

    @Transactional(readOnly = true)
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUser(Long id){return userRepository.findById(id).get();}

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) throws UserNotFoundException {
        User u = userRepository.findByUsername(username);
        if(u == null) {
            throw new UserNotFoundException();
        }
        return u;
    }

    @Transactional(rollbackFor = {EmailAlreadyExistsException.class, UsernameAlreadyExistsException.class, KeycloakErrorRegisterUser.class})
    public User addUser(DTOUser user) throws EmailAlreadyExistsException, UsernameAlreadyExistsException, KeycloakErrorRegisterUser {
        if(userRepository.existsByEmail(user.getEmail())) throw new EmailAlreadyExistsException();
        if(userRepository.existsByUsername(user.getUsername())) throw new UsernameAlreadyExistsException();

        User userResult = new User();

        userResult.setFirstName(user.getFirstName());
        userResult.setLastName(user.getLastName());
        userResult.setTelephone(user.getTelephone());
        userResult.setEmail(user.getEmail());
        userResult.setUsername(user.getUsername());

        Cart c = new Cart();
        c.setOwner(userResult);
        userResult.setCart(c);

        addUserToKeycloak(user);

        return userRepository.save(userResult);
    }

    @Transactional(rollbackFor = KeycloakErrorRegisterUser.class)
    public void addUserToKeycloak(DTOUser user) throws KeycloakErrorRegisterUser {

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(usernameAdmin)
                .password(passwordAdmin)
                .build();

        UserRepresentation userk = new UserRepresentation();

        userk.setEnabled(true);
        userk.setUsername(user.getUsername());
        userk.setEmail(user.getEmail());
        userk.setFirstName(user.getFirstName());
        userk.setLastName(user.getLastName());
        userk.setEmailVerified(true);

        userk.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersRessource = realmResource.users();

        Response response = usersRessource.create(userk);
        if(response.getStatus() != 201)
            throw new KeycloakErrorRegisterUser();
        System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
        System.out.println(response.getLocation());
        String userId = CreatedResponseUtil.getCreatedId(response);
        System.out.printf("User created with userId: %s%n", userId);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(user.getPassword());

        UserResource userResource = usersRessource.get(userId);

        userResource.resetPassword(passwordCred);

        ClientRepresentation app1Client = realmResource.clients().findByClientId(clientId).get(0);

        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()).roles().get(role).toRepresentation();

        userResource.roles().clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));
    }
}
