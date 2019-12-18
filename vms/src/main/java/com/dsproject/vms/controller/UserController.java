package com.dsproject.vms.controller;

import com.dsproject.vms.model.User;
import com.dsproject.vms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseStatus(code = HttpStatus.CREATED,reason = "user created")
    @PostMapping("/register")
    public @ResponseBody User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/")
    public @ResponseBody
    ResponseEntity<Iterable<User>> getUsers() {
        return userService.getUsers();
    }
}
