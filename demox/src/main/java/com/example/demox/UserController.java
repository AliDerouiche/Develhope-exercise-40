package com.example.demox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<UserEntity> create(@RequestBody UserEntity user) {
        return ResponseEntity.ok().body(userRepository.save(user));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<UserEntity> update(@PathVariable Long id, @RequestBody UserEntity user) {
        return userRepository.findById(id).map(foundUser -> {
            foundUser.setBirthDate(user.getBirthDate());
            foundUser.setEmail(user.getEmail());
            foundUser.setFullName(user.getFullName());
            foundUser.setPhoneNumber(user.getPhoneNumber());
            return ResponseEntity.ok().body(userRepository.save(foundUser));
        }).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(userEntity -> {
                    userRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<UserEntity>> getAll() {
        return userRepository.findAll().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(userRepository.findAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserEntity> getSingle(@PathVariable Long id) {
        return userRepository.findById(id).isPresent() ? ResponseEntity.ok().body(userRepository.findById(id).get()) :
                ResponseEntity.notFound().build();
    }

}