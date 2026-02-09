package it.io.demo.model;

import it.io.demo.annotation.OwnerId;
import it.io.demo.annotation.SensitiveData;
import lombok.Data;
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
    private String value;
    private String category;
    private Boolean to_change;

    @OwnerId
    private String ownerId;
}
