package it.io.demo.service;

import it.io.demo.annotation.OwnerId;
import it.io.demo.annotation.SensitiveData;
import it.io.demo.dto.SecretDTO;
import it.io.demo.exception.ResourceNotFoundException;
import it.io.demo.model.Secret;
import it.io.demo.repository.VaultRepository;
import org.springframework.stereotype.Service;
import it.io.demo.utils.SecuirtyUtils;

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

    //Salvataggio
    public SecretDTO saveSecret(SecretDTO secretDTO) throws IllegalAccessException {
        // Conversione SecretDTO -> Secret
        Secret secret = convertToEntity(secretDTO);
        injectOwnerId(secret);

        Secret savedSecret = vaultRepository.save(secret);

        return convertToDTO(savedSecret);
    }

    public Secret findById(String id){
        return vaultRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Secret not found"));
    }

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

        //Controllo della presenza del campo @OwnerId
        for(Field field : fields){
            if(field.isAnnotationPresent(OwnerId.class)){
                try {
                    //Campi privati accessibli
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

        secret.setValue(cryptoService.encrypt(secretDTO.getValue()));
        return secret;
    }
}