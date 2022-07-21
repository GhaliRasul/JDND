package com.example.demo.controllers;

import org.apache.catalina.authenticator.NonLoginAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;


@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final Logger log= LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private CartRepository cartRepository;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id, Authentication authentication) {
		Optional<User> optionalUser = userRepository.findById(id);
		if(optionalUser.isPresent()){
			if(authentication.getName().equals(optionalUser.get().getUsername())){
				log.info("A User with the id " + id+ " was retrieved.");
				return ResponseEntity.of(userRepository.findById(id));
			}
			else {
				log.error("You Are not authenticated");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

		}
			log.warn("A User with the Id: " + id+ " not found.");
			return  ResponseEntity.notFound().build();
		}

	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username, Authentication authentication  ) {
		if(authentication.getName().equals(username)){

			User user = userRepository.findByUsername(username);
			log.info("A User with the username " + username+ " was retrieved.");
			return ResponseEntity.ok(user);
		}
		else {
			log.error("You Are not authenticated");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if(createUserRequest.getPassword().length()<8 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.warn("ERROR - Either length is less than 7 or pass and conf pass do not match. Unable to create " +
					createUserRequest.getUsername()+ "At UserController->createUser");
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		log.info("A User with the username " + createUserRequest.getUsername()+ " was created.");
		return ResponseEntity.ok(user);
	}
	
}
