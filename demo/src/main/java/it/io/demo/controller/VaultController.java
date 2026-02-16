package it.io.demo.controller;

import it.io.demo.dto.SecretDTO;
import it.io.demo.model.Secret;
import it.io.demo.service.VaultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class VaultController {
   private final VaultService vaultService;

    public VaultController(VaultService vaultService){
       this.vaultService = vaultService;
    }

    // ---POST---
   @PostMapping
   public ResponseEntity<SecretDTO> createSecret(@RequestBody SecretDTO secretDTO) throws IllegalAccessException {
        SecretDTO savedSecret = vaultService.saveSecret(secretDTO);
       return new ResponseEntity<>(savedSecret, HttpStatus.CREATED);
   }

   // ---GET---
   @GetMapping("/getById/{id}")
    public ResponseEntity<SecretDTO> getSecretById(@PathVariable String id) throws IllegalAccessException {
       SecretDTO secretDTO = vaultService.findById(id);

       return new ResponseEntity<>(secretDTO, HttpStatus.OK);
   }

   @GetMapping("/getAll")
    public ResponseEntity<List<SecretDTO>> gettAllSecretsByOwnerId() throws IllegalAccessException {
       List<SecretDTO> secretsDTO = vaultService.gettAllSecretsByOwnerId();

       return new ResponseEntity<>(secretsDTO, HttpStatus.OK);
   }

   @GetMapping("/getByName/{name}")
   public ResponseEntity<List<SecretDTO>> getSecretByName(@PathVariable String name) throws IllegalAccessException {
        List<SecretDTO> secretDTO = vaultService.findByName(name);

        return new ResponseEntity<>(secretDTO, HttpStatus.OK);
   }

   @GetMapping("/getByUsername/{username}")
   public ResponseEntity<List<SecretDTO>> getSecretByUsername(@PathVariable String username) throws IllegalAccessException {
        List<SecretDTO> secretDTO = vaultService.findByUsername(username);

        return new ResponseEntity<>(secretDTO, HttpStatus.OK);
   }

   @GetMapping("/getByToChange/{change}")
   public ResponseEntity<List<SecretDTO>> getSecretByToChange(@PathVariable Boolean change) throws IllegalAccessException {
        List<SecretDTO> secretDTO = vaultService.findByToChange(change);

        return new ResponseEntity<>(secretDTO, HttpStatus.OK);
   }

   @GetMapping("/getByCategory/{category}")
   public ResponseEntity<List<SecretDTO>> getSecretByCategory(@PathVariable String category) throws IllegalAccessException {
        List<SecretDTO> secretDTO = vaultService.findByCategory(category);

        return new ResponseEntity<>(secretDTO, HttpStatus.OK);
   }

   // ---DELETE---
   @DeleteMapping("/{id}")
    public void deleteSecretById(@PathVariable String id){
        vaultService.delete(id);
   }

   // ---PUT---
    @PutMapping("/{id}")
    public ResponseEntity<SecretDTO> updateSecret(@PathVariable String id, @RequestBody SecretDTO secretDTO) throws IllegalAccessException {
        SecretDTO updateSecretDTO = vaultService.updateSecret(id, secretDTO);

        return new ResponseEntity<>(updateSecretDTO, HttpStatus.OK);
    }
}
