package com.example.demo.controllerTesting;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerTesting {

    //for UserController
private UserController userController;

    private  Authentication authentication = mock(Authentication.class);

    //for UserController
    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    //for CartController
    private CartController cartController;
    private UserRepository userRepositoryForCart = mock(UserRepository.class);
    private ItemRepository itemRepositoryForCart = mock(ItemRepository.class);
    private CartRepository cartRepositoryForCart = mock(CartRepository.class);
    //for ItemController
    private ItemController itemController;
    private ItemRepository itemRepositoryForItem = mock(ItemRepository.class) ;
    //for OrderController
   private OrderController orderController;

    private UserRepository userRepositoryForOrder = mock(UserRepository.class);

    private OrderRepository orderRepositoryForOrder=mock(OrderRepository.class);
    @BeforeEach
    public void setup() {
        userController = new UserController();
        cartController = new CartController();
        itemController = new ItemController();
        orderController= new OrderController();
        TestUtils.initObject(userController, "userRepository", userRepository);
        TestUtils.initObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        TestUtils.initObject(userController, "cartRepository", cartRepository);
        TestUtils.initObject(cartController, "itemRepository", itemRepositoryForCart);
        TestUtils.initObject(cartController, "userRepository", userRepositoryForCart);
        TestUtils.initObject(cartController, "cartRepository", cartRepositoryForCart);
        TestUtils.initObject(itemController, "itemRepository", itemRepositoryForItem);
        TestUtils.initObject(orderController, "userRepository", userRepositoryForOrder);
        TestUtils.initObject(orderController, "orderRepository", orderRepositoryForOrder);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(getuser()));
        Mockito.when(userRepository.findByUsername("test")).thenReturn(new ResponseEntity<>(getuser(),
                HttpStatus.OK).getBody());
        when(bCryptPasswordEncoder.encode("password")).thenReturn("password");
        when(userRepositoryForCart.findByUsername("test")).thenReturn(getuser());
        when(itemRepositoryForCart.findById(1L)).thenReturn(Optional.of(getItem()));
        when(cartRepositoryForCart.save(getuser().getCart())).thenReturn(getuser().getCart());
        when(itemRepositoryForItem.findById(1L)).thenReturn(Optional.of(getItem()));
        when(itemRepositoryForItem.findAll()).thenReturn(Arrays.asList(getItem()));
        when(itemRepositoryForItem.findByName("something")).thenReturn(Arrays.asList(getItem()));
        when(userRepositoryForOrder.findByUsername("test")).thenReturn(getuser());
        when(orderRepositoryForOrder.findByUser(any())).thenReturn(Arrays.asList(getOrder()));
        when(authentication.getName()).thenReturn("test");
    }

    @Test
    public void create_user_happy_path() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        User user =response.getBody();
        Assert.assertEquals(user.getUsername(),createUserRequest.getUsername());
        Assert.assertEquals(user.getPassword(),"password");

    }
    @Test
    public void find_by_username(){
        //happy Path
        ResponseEntity<User> response=userController.findByUserName("test",authentication);
        Assert.assertNotNull(response);
        Assert.assertEquals(200,response.getStatusCodeValue());
        User user = response.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals("test",user.getUsername());

        //sad Path
        ResponseEntity<User> response2=userController.findByUserName("newTest",authentication);
        Assert.assertNotNull(response2);
        Assert.assertEquals(404,response2.getStatusCodeValue());

    }
    @Test
    public void find_by_Id(){
        //happy Path
        ResponseEntity<User> response=userController.findById(1L,authentication);
        Assert.assertNotNull(response);
        Assert.assertEquals(200,response.getStatusCodeValue());
        User user = response.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals("test",user.getUsername());

        //sad Path
        ResponseEntity<User> response2=userController.findById(2L,authentication);
        Assert.assertNotNull(response2);
        Assert.assertEquals(404,response2.getStatusCodeValue());
    }


    @Test
    public void add_to_cart() {
        //happyPath
       ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
       modifyCartRequest.setUsername("test");
       modifyCartRequest.setItemId(1);
       modifyCartRequest.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest,authentication);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        Assert.assertNotNull(cart);
        Assert.assertEquals("something",cart.getItems().get(0).getName());
        //sadPath username not available
        modifyCartRequest.setUsername("newName");
        ResponseEntity<Cart> response2 = cartController.addTocart(modifyCartRequest,authentication);
        Assert.assertNotNull(response2);
        Assert.assertEquals(404, response2.getStatusCodeValue());
        //sad PAth Item not Available
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(2L);
        ResponseEntity<Cart> response3 = cartController.addTocart(modifyCartRequest,authentication);
        Assert.assertNotNull(response3);
        Assert.assertEquals(404, response3.getStatusCodeValue());
    }

    @Test
    public void remove_from_Cart(){
        //happy path
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest,authentication);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        Assert.assertNotNull(response.getBody());
        //sadPath username not available
        modifyCartRequest.setUsername("newName");
        ResponseEntity<Cart> response2 = cartController.removeFromcart(modifyCartRequest,authentication);
        Assert.assertNotNull(response2);
        Assert.assertEquals(404, response2.getStatusCodeValue());
        //sad PAth Item not Available
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(2L);
        ResponseEntity<Cart> response3 = cartController.removeFromcart(modifyCartRequest,authentication);
        Assert.assertNotNull(response3);
        Assert.assertEquals(404, response3.getStatusCodeValue());

    }

    @Test
    public void get_item_by_id(){
        //happy Path
        ResponseEntity<Item> response =itemController.getItemById(1L);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Item item = response.getBody();
        Assert.assertEquals("something",item.getName());
        //sad PAth id not available
        ResponseEntity<Item> response2 =itemController.getItemById(2L);
        Assert.assertNotNull(response2);
        Assert.assertNull(response2.getBody());
    }
    @Test
    public void get_item_by_name(){
        //happy Path
        ResponseEntity<List<Item>> response =itemController.getItemsByName("something");
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        Assert.assertEquals("something",items.get(0).getName());
        //sad PAth name not available
        ResponseEntity<List<Item>> response2 =itemController.getItemsByName("newThing");
        Assert.assertNotNull(response2);
        Assert.assertNull(response2.getBody());
    }

    @Test
    public void get_Items(){
        //happy Path
        ResponseEntity<List<Item>> response =itemController.getItems();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        Assert.assertEquals("something",items.get(0).getName());

    }

    @Test
    public void submit_Order(){
        //happy path
        ResponseEntity<UserOrder>  response = orderController.submit("test",authentication);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        Assert.assertEquals(order.getItems().get(0),getItem());
        Assert.assertEquals("something",order.getItems().get(0).getName());
        //sad path username not available
        ResponseEntity<UserOrder>  response2 = orderController.submit("newName",authentication);
        Assert.assertNotNull(response2);
        Assert.assertEquals(404, response2.getStatusCodeValue());
    }

    @Test
    public void get_Order_by_username(){
        //happy path
        ResponseEntity<List<UserOrder>>  response = orderController.getOrdersForUser("test",authentication);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        Assert.assertNotNull(orders.get(0).getItems());
        Assert.assertEquals("something", orders.get(0).getItems().get(0).getName());
        Assert.assertEquals(getItem().getPrice(),orders.get(0).getTotal());
        // sad path username not available
        ResponseEntity<List<UserOrder>>  response2 = orderController.getOrdersForUser("NewName",authentication);
        Assert.assertNotNull(response2);
        Assert.assertEquals(404, response2.getStatusCodeValue());

    }



    User getuser(){
        User user = new User();
        Cart cart = new Cart();
        cart.addItem(getItem());
                user.setId(1L);
                user.setCart(cart);
                user.setUsername("test");
                user.setPassword("password");
                return user;
    }

    Item getItem(){
        Item item= new Item();
        item.setId(1L);
        item.setName("something");
        item.setPrice(BigDecimal.valueOf(23.23));
        item.setDescription("something to descripe");
        return item;
    }

    UserOrder getOrder(){
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(getuser());
        userOrder.setItems(Arrays.asList(getItem()));
        userOrder.setTotal(getItem().getPrice());
        userOrder.setId(1L);
        return userOrder;

    }


}
