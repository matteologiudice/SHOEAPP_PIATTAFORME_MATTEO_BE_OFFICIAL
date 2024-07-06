package com.ShoeAppBE.services.user;

import com.ShoeAppBE.models.User;
import com.ShoeAppBE.repositories.UserRepository;
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

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

@Service
public class KeycLoakService {

    @Autowired
    UserRepository userRepository;

    String usernameAdmin = "matteadmin";
    String passwordAdmin = "matteo";
    String role = "customer";
    String serverUrl = "http://localhost:8080";
    String realm = "provaFinaleAngular";
    String clientId = "angular";
    String clientSecret = "";

    public void addUser(User userToAdd,String password){
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(usernameAdmin)
                .password(passwordAdmin)
                .build();

        UserRepresentation user = new UserRepresentation();

        user.setEnabled(true);
        user.setUsername(userToAdd.getUsername());
        user.setEmail(userToAdd.getEmail());
        user.setFirstName(userToAdd.getFirstName());
        user.setLastName(userToAdd.getLastName());
        user.setEmailVerified(true);

        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));

        // Prendere il ream
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersRessource = realmResource.users();

        // Crea Utente
        Response response = usersRessource.create(user);
        System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
        System.out.println(response.getLocation());
        String userId = CreatedResponseUtil.getCreatedId(response);
        System.out.printf("User created with userId: %s%n", userId);

        // Settaggio pass
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);

        UserResource userResource = usersRessource.get(userId);

        userResource.resetPassword(passwordCred);

        //Assegnazione del ruolo
        ClientRepresentation app1Client = realmResource.clients().findByClientId(clientId).get(0);


        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()).roles().get(role).toRepresentation();


        userResource.roles().clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));

    }
}
