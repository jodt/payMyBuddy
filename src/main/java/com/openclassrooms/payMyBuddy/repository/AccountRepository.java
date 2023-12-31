package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByUserMail(String mail);

}
