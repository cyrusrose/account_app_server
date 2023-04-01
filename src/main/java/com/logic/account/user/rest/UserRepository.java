package com.logic.account.user.rest;

import org.springframework.stereotype.Repository;

import com.logic.account.user.User;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLoginAndPassword(String login, String password);

    boolean existsByLoginAndPassword(String login, String password);

    @Query("""
        SELECT u 
        FROM User u JOIN FETCH u.client c LEFT JOIN FETCH c.defaultAccount da
            LEFT JOIN FETCH da.account ac LEFT JOIN FETCH da.currency
        WHERE u.login = :login AND u.password = :password
    """)
    Optional<User> findByLoginAndPasswordWithClient(@Param("login") String login, @Param("password") String password);
}
