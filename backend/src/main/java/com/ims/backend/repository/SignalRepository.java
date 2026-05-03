package com.ims.backend.repository;

import com.ims.backend.mongo.SignalLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SignalRepository extends MongoRepository<SignalLog, String> {
}