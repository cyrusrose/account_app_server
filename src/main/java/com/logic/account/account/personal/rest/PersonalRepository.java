package com.logic.account.account.personal.rest;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logic.account.account.personal.PersonalAccount;
import com.logic.account.user.Client;

public interface PersonalRepository extends 
    JpaRepository<PersonalAccount, UUID>
{
    public void deleteByIdAndClientId(UUID persId, UUID clntId);

    Optional<PersonalAccount> findById(UUID id);
    
    Optional<PersonalAccount> findByIdAndClientId(UUID persId, UUID clntId);

    @Query("SELECT c FROM PersonalCard c WHERE c.cardNo = :cardNo")
    Optional<PersonalAccount> findCardByCardNo(@Param("cardNo") BigInteger cardNo);
    
    @Query("SELECT c FROM PersonalDeposit c WHERE c.accountNo = :accountNo")
    Optional<PersonalAccount> findDepositByAccountNo(@Param("accountNo") BigInteger accountNo);

    Optional<PersonalAccount> findFirstByClientId(UUID clientId);

    Optional<PersonalAccount> findFirstByClient(Client client);

    @Query("SELECT c FROM PersonalAccount c JOIN FETCH c.client JOIN FETCH c.account WHERE c.id = :id")
    Optional<PersonalAccount> findByIdWithAll(UUID id);

    @Query("SELECT c FROM PersonalCard c JOIN FETCH c.client JOIN FETCH c.account WHERE c.cardNo = :cardNo")
    Optional<PersonalAccount> findCardByCardNoWithAll(@Param("cardNo") BigInteger cardNo);

    @Query("SELECT c FROM PersonalDeposit c JOIN FETCH c.client JOIN FETCH c.account WHERE c.accountNo = :accountNo")
    Optional<PersonalAccount> findDepositByAccountNoWithAll(@Param("accountNo") BigInteger accountNo);

    @Query("SELECT c FROM PersonalAccount c JOIN FETCH c.account acc JOIN FETCH c.currency WHERE c.client.id = ?1")
    List<PersonalAccount> findByClientId(UUID clientId);

    @Query("SELECT CASE WHEN count(c) > 0 THEN true ELSE false END FROM PersonalCard c WHERE c.cardNo = ?1")
    boolean existsByCardNo(BigInteger cardNo);

    @Query("SELECT CASE WHEN count(c) > 0 THEN true ELSE false END FROM PersonalClientAccount c WHERE c.accountNo2 = ?1")
    boolean existsByAccountNo2(BigInteger accountNo);

    @Query("SELECT CASE WHEN count(c) > 0 THEN true ELSE false END FROM PersonalDeposit c WHERE c.accountNo = ?1")
    boolean existsByAccountNo(BigInteger accountNo);

    @Query("SELECT CASE WHEN count(c) > 1 THEN true ELSE false END FROM PersonalAccount c WHERE c.client = ?1")
    boolean isPresent(Client clnt);
}
