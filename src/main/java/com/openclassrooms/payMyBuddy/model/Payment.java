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
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double amount;

    private String description;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "issuer_account_id", referencedColumnName = "id")
    private Account issuerAccount;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "receiver_account_id", referencedColumnName = "id")
    private Account receiverAccount;

}
