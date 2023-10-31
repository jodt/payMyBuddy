package com.openclassrooms.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@ToString(exclude = "transfer")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_account")
@Builder
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String iban;

    @OneToMany(
            orphanRemoval = true,
            fetch = FetchType.EAGER,
            mappedBy = "bankAccount"
    )
    private List<Transfer> transfer;

    @OneToOne(
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "user_id")
    private User user;

}
