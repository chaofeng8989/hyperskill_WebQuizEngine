package engine.controller;

import engine.securit.Authority;
import engine.model.User;
import engine.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserController() {
    }

    @PostMapping (path = "/api/register",  consumes = "application/json")
    public ResponseEntity addUser(@Valid @RequestBody User user) {
        System.out.println("in login field");
        User has = userRepository.getUserByUsername(user.getEmail());
        if (has != null) {
            return ResponseEntity.badRequest().build();
        }
        Authority authority = new Authority();
        authority.setName("USER");

        User store = new User();
        store.setEmail(user.getEmail().toString());
        store.setPassword(bCryptPasswordEncoder.encode(user.getPassword()).toString());
        store.setAuthorities(Stream.of(authority).collect(Collectors.toList()));
        store.setUsername(user.getEmail().toString());
        System.out.println(user);
        System.out.println(store);

        userRepository.save(store);
        return ResponseEntity.ok(store);
    }

}
