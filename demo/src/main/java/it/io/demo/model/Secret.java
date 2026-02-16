package it.io.demo.model;

import it.io.demo.annotation.OwnerId;
import it.io.demo.annotation.SensitiveData;
import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "vault")
public class Secret {
    @Id
    private String id;
    private String name;
    private String username;
    @SensitiveData
    private Binary value;
    private String category;
    private Boolean toChange;

    @OwnerId
    private String ownerId;
}
