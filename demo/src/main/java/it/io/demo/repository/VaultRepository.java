package it.io.demo.repository;

import it.io.demo.model.Secret;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaultRepository extends MongoRepository<Secret,String> {
    List<Secret> findByOwnerId(String ownerId);
}
