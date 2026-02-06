package it.io.demo.controller;

import it.io.demo.model.Secret;
import it.io.demo.repository.VaultRepository;
import it.io.demo.service.VaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vault")
public class VaultController {
   private final VaultService vaultService;

   public VaultController(VaultService vaultService){
       this.vaultService = vaultService;
   }

   @PostMapping
   public ResponseEntity<Secret> createSecret(@RequestBody Secret secret){
       Secret savedSecret = vaultService.saveSecret(secret);

       return new ResponseEntity<>(savedSecret, HttpStatus.CREATED);
   }

   @GetMapping("/{id}")
    public ResponseEntity<Secret> getSecretById(@PathVariable String id){
       Secret secret = vaultService.findById(id);

       return new ResponseEntity<>(secret, HttpStatus.OK);
   }

}
