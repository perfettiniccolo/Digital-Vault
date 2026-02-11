package it.io.demo.config;

import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
public class KeySetupRunner implements CommandLineRunner {
    public final ClientEncryption clientEncryption;

    public KeySetupRunner(ClientEncryption clientEncryption) {
        this.clientEncryption = clientEncryption;
    }

    @Override
    public void run(String... args) throws Exception {
        // Nominativo della dek
        String keyName = "vault-data-key";

        System.out.println("--- CONTROLLO CHIAVI DI CIFRATURA ---");

        // Interrogazione al db se esiste la chiave all'interno
        BsonDocument existingKey = clientEncryption.getKeyByAltName(keyName);

        if (existingKey == null) {
            System.out.println("--- NO EXISTEN KEY ---");

            DataKeyOptions options = new DataKeyOptions();
            options.keyAltNames(List.of(keyName));

            // Creazione chiave
            // Usa il provider "local" (la Master Key) per cifrare questa nuova chiave
            BsonBinary dataKeyId = clientEncryption.createDataKey("local", options);

            // Conversione dell'ID in stringa leggibile (Base64) per usarla dopo
            String base64Id = Base64.getEncoder().encodeToString(dataKeyId.getData());

            System.out.println("NUOVA CHIAVE GENERATA");
            System.out.println("ID: " + base64Id);
        }
        else{
            BsonBinary id = existingKey.getBinary("_id");
            String base64Id = Base64.getEncoder().encodeToString(id.getData());

            System.out.println(" Chiave esistente con id: " + base64Id);
        }
    }
}
