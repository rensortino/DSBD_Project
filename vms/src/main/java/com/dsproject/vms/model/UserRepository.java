package com.dsproject.vms.model;

import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, ObjectId> {
    User findByEmail(String email);
}
