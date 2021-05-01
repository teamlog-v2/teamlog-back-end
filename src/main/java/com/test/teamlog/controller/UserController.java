package com.test.teamlog.controller;

import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "*" )
public class UserController {
    private final UserService userService;

    // Read
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.UserResponse> getUserById(@PathVariable("id") String id) {
        UserDTO.UserResponse userResponse = userService.getUser(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    // Create
    @PostMapping("")
    public ResponseEntity<UserDTO.UserResponse> signUp(@Valid @RequestBody UserDTO.UserRequest userRequest) {
        userService.signUp(userRequest);
        UserDTO.UserResponse userResponse = userService.getUser(userRequest.getId());
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO.UserResponse> updateUser(@PathVariable("id") String id, @Valid @RequestBody UserDTO.UserRequest userRequest) {
        userService.updateUser(id, userRequest);
        UserDTO.UserResponse userResponse = userService.getUser(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("id") String id) {
        ApiResponse apiResponse = userService.deleteUser(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}