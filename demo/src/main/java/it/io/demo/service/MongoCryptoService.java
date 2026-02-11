package it.io.demo.service;

import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import org.springframework.beans.factory.annotation.Value;
import org.bson.BsonBinary;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@Service
public class MongoCryptoService {
    private final ClientEncryption clientEncryption;
    private final UUID dataKeyUuid;

    // Algoritmo randomico
    private static final String ALGORITHM = "AEAD_AES_256_CBC_HMAC_SHA_512-Random";

    public MongoCryptoService(ClientEncryption clientEncryption, @Value("${mongodb.encryption.data-key-id}") String base64DataKeyId) {
        this.clientEncryption = clientEncryption;

        byte[] decoded = Base64.getDecoder().decode(base64DataKeyId);
        this.dataKeyUuid = asUuid(decoded);
    }

    private UUID asUuid(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    // Cifratura
    public Binary encrypt(String value) {
        if(value==null) return null;

        // Conversione dell'UUID della chiave nel formato BSON
        BsonBinary dataKeyId = new BsonBinary(dataKeyUuid);

        // Esecuzione della cifratura usando l'algoritmo randomico
        BsonBinary encryptedValue = clientEncryption.encrypt(
                new BsonString(value),
                new EncryptOptions(ALGORITHM).keyId(dataKeyId)
        );

        // Conversione in binary in modo da essere gestito da Spring
        return new Binary(encryptedValue.getType(), encryptedValue.getData());
    }

    // Decifratura
    public String decrypt(Object value) {
        if(value==null) return null;

        if(!(value instanceof Binary)){
            return value.toString();
        }

        // Prendiamo il pacchetto Binary
        Binary binary = (Binary)value;

        // Conversione in BSON Binary per farlo leggere al decifratore
        BsonBinary bsonBinary = new BsonBinary(binary.getType(), binary.getData());

        BsonValue decryptedValue = clientEncryption.decrypt(bsonBinary);

        if(decryptedValue.isString()){
            return decryptedValue.asString().getValue();
        }
        else {
            return decryptedValue.toString();
        }
    }
}
