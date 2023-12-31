package com.openclassrooms.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Transfer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double amount;

    @Enumerated(EnumType.STRING)
    private OperationEnum operation;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "account_id")
    private Account account;

}
