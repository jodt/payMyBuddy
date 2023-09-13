package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
