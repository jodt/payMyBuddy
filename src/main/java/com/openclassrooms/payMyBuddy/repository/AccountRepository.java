package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

}
