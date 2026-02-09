package it.io.demo.controller;

import it.io.demo.exception.ResourceNotFoundException;
import it.io.demo.model.Secret;
import it.io.demo.repository.VaultRepository;
import it.io.demo.service.VaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class VaultController {
   private final VaultService vaultService;
    private final VaultRepository vaultRepository;

    public VaultController(VaultService vaultService, VaultRepository vaultRepository){
       this.vaultService = vaultService;
        this.vaultRepository = vaultRepository;
    }

    // ---POST---

   @PostMapping
   public ResponseEntity<Secret> createSecret(@RequestBody Secret secret){
       Secret savedSecret = vaultService.saveSecret(secret);

       return new ResponseEntity<>(savedSecret, HttpStatus.CREATED);
   }

   // ---GET---

   @GetMapping("/{id}")
    public ResponseEntity<Secret> getSecretById(@PathVariable String id){
       Secret secret = vaultService.findById(id);

       return new ResponseEntity<>(secret, HttpStatus.OK);
   }

   @GetMapping("/getAll/{ownerId}")
    public ResponseEntity<List<Secret>> gettAllSecretsByOwnerId(@PathVariable String ownerId){
       List<Secret> secrets = vaultService.gettAllSecretsByOwnerId(ownerId);

       return new ResponseEntity<>(secrets, HttpStatus.OK);
   }

   // ---DELETE---
   @DeleteMapping("/{secretId}")
    public void deleteSecretById(@PathVariable String secretId){
       if(!vaultRepository.existsById(secretId)){
           throw new ResourceNotFoundException("Secret not found");
       }

       Secret secret = vaultService.findById(secretId);

       vaultRepository.delete(secret);
   }

   // ---PUT---
    @PutMapping
    public ResponseEntity<Secret> updateSecret(@RequestBody Secret secret){
        Secret existingSecret = vaultRepository.findById(secret.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Secret not found"));

        try {
            for (Field field : Secret.class.getDeclaredFields()) {
                field.setAccessible(true);

                Object newValue = field.get(secret);

                if (newValue != null && !field.getName().equals("id") && !field.getName().equals("ownerId")) {
                    field.set(existingSecret, newValue);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Errore durante l'aggiornamento dei campi", e);
        }

        return new ResponseEntity<>(vaultRepository.save(existingSecret), HttpStatus.OK);
    }
}
