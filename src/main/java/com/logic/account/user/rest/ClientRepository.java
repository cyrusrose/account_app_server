package com.logic.account.user.rest;

import com.logic.account.user.Client;
import com.logic.account.user.phone.Phone;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    @Query("SELECT c FROM Customer c JOIN FETCH c.user WHERE c.phone = ?1")
    public Optional<Client> findByPhone(Phone phone);

    @Query("SELECT c FROM PersonalCard pa JOIN pa.client c WHERE pa.cardNo = ?1")
    public Optional<Client> findByCard(BigInteger cardNo);

    @Query("SELECT c FROM CorporateCustomer c JOIN c.clientNos nos WHERE c.clientSsn = ?1 AND nos.clientNo = ?2")
    public Optional<Client> findByClientNo(BigInteger clientSsn, BigInteger clientNo);

    @Query("SELECT c FROM CorporateCustomer c WHERE c.clientSsn = ?1")
    public Optional<Client> findByClientSsn(BigInteger clientSsn);

    @Query("SELECT c FROM Client c JOIN FETCH c.user WHERE c.id = ?1")
    public Optional<Client> findByIdWith(UUID id);

    @Query("SELECT c.client FROM PersonalAccount c WHERE c.id = ?1")
    public Optional<Client> findClientByPersonalAccountId(UUID id);
}
