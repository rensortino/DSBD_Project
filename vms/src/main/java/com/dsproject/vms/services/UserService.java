package com.dsproject.vms.services;

import com.dsproject.vms.model.User;
import com.dsproject.vms.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
public class UserService {

    @Autowired
    UserRepository repo;

    @ResponseBody
    public User createUser(User user) {
        return repo.save(user);
    }

    public @ResponseBody
    ResponseEntity<Iterable<User>> getUsers() {
        return ResponseEntity.ok(repo.findAll());
    }
}
