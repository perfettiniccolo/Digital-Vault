package it.io.demo.service;

import it.io.demo.annotation.OwnerId;
import it.io.demo.annotation.SensitiveData;
import it.io.demo.dto.SecretDTO;
import it.io.demo.exception.ResourceNotFoundException;
import it.io.demo.model.Secret;
import it.io.demo.repository.VaultRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import it.io.demo.utils.SecuirtyUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class VaultService {
    private final VaultRepository vaultRepository;
    private final MongoCryptoService cryptoService;

    public VaultService(VaultRepository vaultRepository, MongoCryptoService mongoCryptoService) {
        this.vaultRepository = vaultRepository;
        this.cryptoService = mongoCryptoService;
    }

    public Secret findById(String id){
        return vaultRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Secret not found"));
    }

    // Salvataggio
    public SecretDTO saveSecret(SecretDTO secretDTO) throws IllegalAccessException {
        // Conversione SecretDTO -> Secret
        Secret secret = convertToEntity(secretDTO);
        injectOwnerId(secret);

        Secret savedSecret = vaultRepository.save(secret);

        return convertToDTO(savedSecret);
    }

    // Eliminazione
    public void delete(String id){
        if(!vaultRepository.existsById(id)){
            throw new ResourceNotFoundException("Secret not found");
        }
        vaultRepository.deleteById(id);
    }

    // Modifica
    public SecretDTO updateSecret(String id, SecretDTO secretDTO){
        Secret existingSecret = vaultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Secret not found"));

        try {
            for (Field dtoField : secretDTO.getClass().getDeclaredFields()) {
                Field secretField = existingSecret.getClass().getDeclaredField(dtoField.getName());

                dtoField.setAccessible(true);
                secretField.setAccessible(true);

                Object newValue = dtoField.get(secretDTO);

                if (newValue != null) {
                    if (!secretField.isAnnotationPresent(SensitiveData.class)) {
                        if (!secretField.getName().equals("id") && !secretField.getName().equals("ownerId")) {
                            secretField.set(existingSecret, newValue);
                        }
                    }
                    else {
                        secretField.set(existingSecret, cryptoService.encrypt(secretDTO.getValue()));
                    }
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Errore di accesso ai campi", e);
        }

        vaultRepository.save(existingSecret);

        return convertToDTO(existingSecret);
    }

    // Getter
    public List<SecretDTO> gettAllSecretsByOwnerId() throws IllegalAccessException {
        String correntUserId = SecuirtyUtils.getCurrentUserId();

        List<Secret> secrets = vaultRepository.findByOwnerId(correntUserId);
        List<SecretDTO> secretsDTO = new java.util.ArrayList<>(List.of());

        for (Secret secret : secrets) {
            secretsDTO.add(convertToDTO(secret));
        }

        return secretsDTO;
    }

    public void injectOwnerId(Object enityt){
        String userID = SecuirtyUtils.getCurrentUserId();

        if(userID==null){
            throw new ResourceNotFoundException("Secret non trovato con ID " + userID);
        }

        //Mappa dell'oggetto
        Class<?> clasz = enityt.getClass();

        //Tutti i campi della classe
        Field[] fields = clasz.getDeclaredFields();

        for(Field field : fields){
            if(field.isAnnotationPresent(OwnerId.class)){
                try {
                    field.setAccessible(true);

                    //Campo della classe riempito
                    field.set(enityt, userID);

                    return;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Convertitori
    public SecretDTO convertToDTO(Secret secret){
        SecretDTO dto = new SecretDTO();

        for (Field dtoField : dto.getClass().getDeclaredFields()) {
            try {
                Field secretField = secret.getClass().getDeclaredField(dtoField.getName());

                if (!secretField.isAnnotationPresent(SensitiveData.class)) {
                    dtoField.setAccessible(true);
                    secretField.setAccessible(true);
                    dtoField.set(dto, secretField.get(secret));
                }
            } catch (NoSuchFieldException e) {
                continue;
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Errore di accesso ai campi", e);
            }
        }

        // DECIFRAZIONE: Binary -> String
        dto.setValue(cryptoService.decrypt(secret.getValue()));
        return dto;
    }

    public Secret convertToEntity(SecretDTO secretDTO) throws IllegalAccessException {
        Secret secret = new Secret();

        for (Field secretField : secret.getClass().getDeclaredFields()) {
            try {
                Field dtoField = secretDTO.getClass().getDeclaredField(secretField.getName());

                if (!secretField.isAnnotationPresent(SensitiveData.class)) {
                    dtoField.setAccessible(true);
                    secretField.setAccessible(true);
                    secretField.set(secret, dtoField.get(secretDTO));
                }
            }  catch (NoSuchFieldException e) {
                continue;
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Errore di accesso ai campi", e);
            }
        }

        // DECIFRAZIONE: String -> Binary
        secret.setValue(cryptoService.encrypt(secretDTO.getValue()));
        return secret;
    }
}