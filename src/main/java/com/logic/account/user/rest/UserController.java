package com.logic.account.user.rest;

import static com.logic.account.utils.MyUtils.makeLocation;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.logic.account.user.Client;
import com.logic.account.user.ClientNumber;
import com.logic.account.user.User;
import com.logic.account.utils.Views;

import lombok.*;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {
    private @Autowired UserService userSrv;
    @Autowired EntityManager em;
    
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") UUID id) {
        val user = userSrv.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/user/auth")
    public Boolean getUserAuth(
        @RequestParam("login") String login, 
        @RequestParam("pw") String password
    ) {
        return userSrv.existsByLoginAndPassword(login, password);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(
        @RequestParam("login") String login, 
        @RequestParam("pw") String password
    ) {
        val user = userSrv.findByLoginAndPassword(login, password);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

	@GetMapping("/user_with_client")
    public ResponseEntity<User> getUserWith(
        @RequestParam("login") String login, 
        @RequestParam("pw") String password
    ) throws JsonProcessingException {
        val user = userSrv.findWithClient(login, password);

        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/user/{id}")
    public void putUser(@PathVariable("id") UUID id, @RequestBody User user) {
        userSrv.update(id, user);
    }

    @PostMapping("/user")
    public ResponseEntity<?> postUser(@RequestBody User user) {
        val result = userSrv.save(user);

        return ResponseEntity
            .created(makeLocation(result)).build();
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<Client> getClient(@PathVariable("id") UUID id) {
        val clnt = userSrv.findClientById(id);
        if (clnt.isPresent()) {
            return ResponseEntity.ok().body(clnt.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @JsonView(Views.Public.class)
    @GetMapping("/client")
    public ResponseEntity<Client> getClient(
        @RequestParam(name = "phone", required = false) String phone,
        @RequestParam(name = "ssn", required = false) BigInteger clientSsn
    ) {
        var clnt = Optional.<Client>empty();
        if(phone != null)
            clnt = userSrv.findByPhone(phone);
        else if(clientSsn != null)
            clnt = userSrv.findByClientSsn(clientSsn);
        
        if (clnt.isPresent()) {
            return ResponseEntity.ok().body(clnt.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @JsonView(Views.Public.class)
    @GetMapping("/client_nos")
    public ResponseEntity<List<ClientNumber>> getClientNos(
        @RequestParam(name = "ssn", required = false) BigInteger clientSsn
    ) {
        val list = userSrv.findClientNosByClientSsn(clientSsn);
        
        if (!list.isEmpty()) {
            return ResponseEntity.ok().body(list);
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(list);
    }

    @PutMapping("/client/{id}")
    public void putClient(@PathVariable("id") UUID id, @RequestBody Client clnt) {
        userSrv.updateClient(id, clnt);
    }

    @PostMapping("/client")
    public ResponseEntity<?> postClient(@RequestBody Client clnt) {
        val result = userSrv.saveClient(clnt);

        return ResponseEntity
            .created(makeLocation(result)).build();
    }
}
