package com.harshit.entity;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import javax.persistence.*;

@Introspected
@Entity
@Table(name="member")
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String name;
    private String email;


}
