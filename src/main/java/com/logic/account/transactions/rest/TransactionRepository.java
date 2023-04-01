package com.logic.account.transactions.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logic.account.transactions.UserTransaction;
import com.logic.account.transactions.dtos.TransactionDto;

import lombok.NonNull;

@NonNull
public interface TransactionRepository extends JpaRepository<UserTransaction, UUID> {
    @Query(
        """
        SELECT c FROM UserTransaction c 
        WHERE (
                (c.senderId = :clientId AND c.state = 'to') 
                OR (c.receiverId = :clientId AND c.state = 'from')
                OR (c.senderId = :clientId AND c.state = 'change')
            )
            AND c.state = (CASE :state WHEN 'both' THEN c.state ELSE :state END) 
            AND c.via LIKE %:via%
        """
    )
    public List<UserTransaction> findByClientId(@Param("clientId") UUID clientId, @Param("via") String via, @Param("state") String state);

    @Query(
        """
        SELECT c FROM UserTransaction c 
        WHERE (
                (c.senderId = :clientId AND c.state = 'to') 
                OR (c.receiverId = :clientId AND c.state = 'from')
                OR (c.senderId = :clientId AND c.state = 'change')
            )
            AND c.state = (CASE :state WHEN 'both' THEN c.state ELSE :state END)
        """
    )
    public List<UserTransaction> findByClientId(@Param("clientId") UUID clientId, @Param("state") String state);
}
