package it.io.demo.service;

import it.io.demo.annotation.OwnerId;
import it.io.demo.exception.ResourceNotFoundException;
import it.io.demo.model.Secret;
import it.io.demo.repository.VaultRepository;
import org.springframework.stereotype.Service;
import it.io.demo.utils.SecuirtyUtils;

import java.lang.reflect.Field;

@Service
public class VaultService {
    private final VaultRepository vaultRepository;

    public VaultService(VaultRepository vaultRepository) {
        this.vaultRepository = vaultRepository;
    }

    //Salvataggio
    public Secret saveSecret(Secret secret){
        // injectOwnerId(secret);

        return vaultRepository.save(secret);
    }

    public Secret findById(String id){
        return vaultRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Secret not found"));
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
}
