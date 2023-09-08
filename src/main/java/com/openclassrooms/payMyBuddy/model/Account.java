package com.openclassrooms.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String number;

    private double balance;

    @ManyToMany(
            fetch = FetchType.EAGER
    )
    @JoinTable(name = "payment",
            joinColumns = @JoinColumn(name = "issuer_account_id"),
            inverseJoinColumns = @JoinColumn(name = "receiver_account_id")
    )
    private List<Payment> payments;

    @OneToMany(
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "account_id")
    private List<Transfer> transfer;

    @OneToOne(
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "user_id")
    private User user;


}
