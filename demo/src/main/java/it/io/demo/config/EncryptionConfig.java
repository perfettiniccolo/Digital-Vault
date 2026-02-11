package it.io.demo.config;

import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class EncryptionConfig {
    @Bean
    public ClientEncryption clientEncryption() {
        // Lettura Master Key
        byte[] localMasterKey = new byte[96];

        try (FileInputStream stream = new FileInputStream("master-key.txt")){
            stream.read(localMasterKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Mappatura master key
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", localMasterKey);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<>();
        kmsProviders.put("local", keyMap);

        // Key vault, dove mongo salverà i dati
        String keyVaultNamespace = "demo.encryption.__keyVault";

        // Client setting per gestire le chiavi
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .build();

        // Client
        ClientEncryptionSettings settings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(clientSettings)
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviders)
                .build();

        return ClientEncryptions.create(settings);
    }
}
