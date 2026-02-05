package it.io.demo.model;

import it.io.demo.annotation.SensitiveData;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotBlank;

@Data // Lombok genera getter, setter e costruttori automaticamente
@Document(collection = "tasks") //Crea una tabella con mongo
public class Task {

    public Task() {}

    @Id
    private String id;

    @NotBlank(message = "Titolo da inserire")
    private String title;
    private String description;
    private Boolean completed;

    public Boolean isCompleted() {
        return completed;
    }
}
