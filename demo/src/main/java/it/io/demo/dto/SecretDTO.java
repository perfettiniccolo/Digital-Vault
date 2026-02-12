package it.io.demo.dto;

import it.io.demo.annotation.SensitiveData;
import lombok.Data;

@Data
public class SecretDTO {
    private String id;
    private String name;
    private String username;
    @SensitiveData
    private String value;
    private String category;
    private Boolean to_change;
    private String ownerId;
}