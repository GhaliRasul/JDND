package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final static Logger log = LoggerFactory.getLogger(CartController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request ,Authentication authentication ) {
		if(authentication.getName().equals(request.getUsername())){
			User user = userRepository.findByUsername(request.getUsername());
			if(user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if(!item.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
					.forEach(i -> cart.addItem(item.get()));
			cartRepository.save(cart);
			log.info(String.format("A Cart is created for Username %s with items %s. At CartController->addToCart."
					,user.getUsername(),
					user.getCart().getItems().stream().map(Item::getName).collect(Collectors.toList())));
			return ResponseEntity.ok(cart);
		}else {
			log.error("You Are not authenticated");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request, Authentication authentication) {
		if(authentication.getName().equals(request.getUsername())){
			User user = userRepository.findByUsername(request.getUsername());
			if(user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if(!item.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
					.forEach(i -> cart.removeItem(item.get()));
			cartRepository.save(cart);
			log.info(String.format("The User %s removed the item %s from the cart. At CartController removeFromcart"
					, user.getUsername(), item.get().getName()));
			return ResponseEntity.ok(cart);


		}else {
			log.error("you are not authenticated");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

	}
		
}
