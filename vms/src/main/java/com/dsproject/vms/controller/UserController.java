package com.dsproject.vms.controller;

import com.dsproject.vms.model.User;
import com.dsproject.vms.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repo;

    @PostMapping("/register")
    public @ResponseBody User createUser(@RequestBody User user) {
        return repo.save(user);
    }
}
