package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Page<Payment> findByIssuerAccountId(int id, Pageable pageable);

}
