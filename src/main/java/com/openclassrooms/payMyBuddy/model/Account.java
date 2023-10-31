package com.openclassrooms.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"transfer", "payments"})
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

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "issuerAccount"
    )
    private List<Payment> payments;

    @OneToMany(
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            mappedBy = "account"
    )
    private List<Transfer> transfer;

    @OneToOne(
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "user_id")
    private User user;


}
