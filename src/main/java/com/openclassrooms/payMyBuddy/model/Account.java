package com.openclassrooms.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String number;

    private double balance;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "issuer_account_id")
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
