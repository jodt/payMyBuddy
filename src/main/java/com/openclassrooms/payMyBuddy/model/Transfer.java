package com.openclassrooms.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class Transfer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double amount;

    @Enumerated(EnumType.STRING)
    private OperationEnum operation;

    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "account_id")
    private Account account;

}
