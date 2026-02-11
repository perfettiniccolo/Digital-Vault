package it.io.demo.service;

import it.io.demo.annotation.OwnerId;
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
    public SecretDTO saveSecret(SecretDTO secretDTO){
        // Conversione SecretDTO -> Secret
        Secret secret = new Secret();
        secret.setName(secretDTO.getName());
        secret.setUsername(secretDTO.getUsername());
        secret.setCategory(secretDTO.getCategory());
        secret.setTo_change(secretDTO.getTo_change());

        injectOwnerId(secret);

        secret.setValue(cryptoService.encrypt(secretDTO.getValue()));

        Secret savedSecret = vaultRepository.save(secret);

        return convertToDTO(savedSecret);
    }

    public Secret findById(String id){
        return vaultRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Secret not found"));
    }

    public List<SecretDTO> gettAllSecretsByOwnerId(){
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

    public SecretDTO convertToDTO(Secret secret) {
        SecretDTO dto = new SecretDTO();
        dto.setId(secret.getId());
        dto.setOwnerId(secret.getOwnerId());
        dto.setName(secret.getName());
        dto.setUsername(secret.getUsername());
        dto.setCategory(secret.getCategory());
        dto.setTo_change(secret.getTo_change());

        // DECIFRAZIONE: Binary -> String
        dto.setValue(cryptoService.decrypt(secret.getValue()));
        return dto;
    }
}