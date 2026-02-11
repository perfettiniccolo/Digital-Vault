package it.io.demo.controller;

import it.io.demo.dto.SecretDTO;
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
   public ResponseEntity<SecretDTO> createSecret(@RequestBody SecretDTO secretDTO) throws IllegalAccessException {
        SecretDTO savedSecret = vaultService.saveSecret(secretDTO);
       return new ResponseEntity<>(savedSecret, HttpStatus.CREATED);
   }

   // ---GET---

   @GetMapping("/{id}")
    public ResponseEntity<SecretDTO> getSecretById(@PathVariable String id) throws IllegalAccessException {
       Secret secret = vaultService.findById(id);

       return new ResponseEntity<>(vaultService.convertToDTO(secret), HttpStatus.OK);
   }

   @GetMapping("/getAll")
    public ResponseEntity<List<SecretDTO>> gettAllSecretsByOwnerId() throws IllegalAccessException {
       List<SecretDTO> secretsDTO = vaultService.gettAllSecretsByOwnerId();

       return new ResponseEntity<>(secretsDTO, HttpStatus.OK);
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

    @PutMapping("/{id}")
    public ResponseEntity<SecretDTO> updateSecret(@PathVariable String id, @RequestBody SecretDTO secretDTO) throws IllegalAccessException {
        SecretDTO secretDTO = vaultService
    }

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
