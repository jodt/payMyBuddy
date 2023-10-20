package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    Optional<BankAccount> findByUserMail(String userEmail);

}
