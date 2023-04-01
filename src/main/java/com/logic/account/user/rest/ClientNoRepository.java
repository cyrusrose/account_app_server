package com.logic.account.user.rest;

import com.logic.account.user.ClientNumber;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientNoRepository extends JpaRepository<ClientNumber, UUID> {
    @Query("SELECT c.clientNos FROM CorporateCustomer c WHERE c.clientSsn = coalesce(?1, c.clientSsn)")
    public List<ClientNumber> findByClientSsn(BigInteger clientSsn);
}
