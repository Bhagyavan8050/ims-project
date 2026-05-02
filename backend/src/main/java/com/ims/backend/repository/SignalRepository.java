package com.ims.backend.repository;

import com.ims.backend.model.Signal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SignalRepository extends MongoRepository<Signal, String> {


}