package it.io.demo.repository;

import it.io.demo.model.Secret;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaultRepository extends MongoRepository<Secret,String> {
}
