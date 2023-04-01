package com.logic.account.user.rest;

import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.logic.account.user.Client;
import com.logic.account.user.ClientNumber;
import com.logic.account.user.User;
import com.logic.account.user.phone.Phone;
import com.logic.account.user.phone.PhoneType;

import lombok.*;

@Service
public class UserService {
    private @Autowired UserRepository userRep;
    private @Autowired ClientRepository clntRep;
    private @Autowired ClientNoRepository noRep;

    private @Autowired ClientMapper clntMapper;
    private @Autowired UserMapper usrMapper;

    public Optional<User> findById(UUID id) {
        return userRep.findById(id);
    }

    public Optional<User> findWithClient(
       String login, String password
    ) throws JsonProcessingException {
        return userRep.findByLoginAndPasswordWithClient(login, password);
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        return userRep.findByLoginAndPassword(login, password);
    }

    public boolean existsByLoginAndPassword(String login, String password) {
        return userRep.existsByLoginAndPassword(login, password);
    }

    public User update(UUID id, User userDetails) {
        return userRep.findById(id).map(user -> {
            usrMapper.update(userDetails, user);
            return userRep.save(user);
        })
        .orElseThrow();
    }

    public User save(User user) {
        return userRep.save(user);
    }

    public Optional<Client> findClientById(UUID id) {
        return clntRep.findByIdWith(id);
    }

    public Client findClientByPersonalAccountId(UUID id) {
        return clntRep.findClientByPersonalAccountId(id).orElseThrow( () -> {
            throw new NoSuchElementException("client is not present for specified account");
        });
    }

    public Optional<Client> findByClientSsn(BigInteger clientSsn) {
        return clntRep.findByClientSsn(clientSsn);
    }

    public Optional<Client> findByPhone(String phone_number) {
        val phone = Phone.of(phone_number);
        if (phone.getType().equals(PhoneType.MALFORMED))
            throw new IllegalArgumentException("%s is malformed".formatted(phone_number));
        
        return clntRep.findByPhone(phone);
    }

    public Client updateClient(UUID id, Client clientDetails) {
        return clntRep.findById(id).map(client -> {
            clntMapper.update(clientDetails, client);
            return clntRep.save(client);
        })
        .orElseThrow();
    }

    public Client saveClient(Client client) {
        return clntRep.save(client);
    }

    public List<ClientNumber> findClientNosByClientSsn(BigInteger clientSsn) {
        return noRep.findByClientSsn(clientSsn);
    }
}
