package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {

}
