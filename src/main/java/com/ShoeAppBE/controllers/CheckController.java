package com.ShoeAppBE.controllers;

import com.ShoeAppBE.authentication.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
public class CheckController {

    @GetMapping("")
    public ResponseEntity check(){
        return new ResponseEntity("CKECK SOLO ADMIN"+ Utils.getName() + " " + Utils.getUsername(), HttpStatus.OK);
    }

    @GetMapping("/notWorking")
    /*
    Per utilizzare @RolesAllowed il ruolo deve iniziare con ROLE_ quindi va
    modificato il converter aggiungedo ad ogni ruolo il prefisso ROLE_.
    Altimenti si può usare @PreAuthorize
     */
    public ResponseEntity check2() {
        return ResponseEntity.ok("OK  ");
    }

    @GetMapping("/prova2")
    public ResponseEntity check4(){
        return ResponseEntity.ok("SIA ADMIN CHE UTENTI PROVA "+
                "\n");
    }

    @GetMapping("/prova")
    public ResponseEntity check3(){
        return ResponseEntity.ok("SIA ADMIN CHE UTENTI PROVA "+
                "\n");
    }
}
