package com.example.demo.controllerTesting;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTesting {


    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setup() {
        userController = new UserController();
        TestUtils.initObject(userController, "userRepository", userRepository);
        TestUtils.initObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        TestUtils.initObject(userController, "cartRepository", cartRepository);
    }

    @Test
    public void create_user_happy_path() {
        when(bCryptPasswordEncoder.encode("passwordTest")).thenReturn("pass");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testname");
        createUserRequest.setPassword("passwordTest");
        createUserRequest.setConfirmPassword("passwordTest");

        ResponseEntity<User> response = userController.createUser(createUserRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        User user =response.getBody();
        Assert.assertEquals(user.getUsername(),createUserRequest.getUsername());
        Assert.assertEquals(user.getPassword(),"pass");
    }
    @Test
    public void find_by_username(){
        when(bCryptPasswordEncoder.encode("passwordTest")).thenReturn("pass");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testname");
        createUserRequest.setPassword("passwordTest");
        createUserRequest.setConfirmPassword("passwordTest");
        ResponseEntity<User>response=userController.createUser(createUserRequest);
        Mockito.when(userRepository.findByUsername("testname")).thenReturn(response.getBody());
        ResponseEntity<User> response2=userController.findByUserName("testname");
        Assert.assertNotNull(response2);
        Assert.assertEquals(200,response.getStatusCodeValue());
        User user = response2.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals("testname",user.getUsername());
    }

}
