package com.wf.training.piggybank.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Payee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String name;
    private String accountNumber;
    private String nickname;

}
