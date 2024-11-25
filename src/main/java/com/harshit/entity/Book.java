package com.harshit.entity;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import javax.persistence.*;

@Introspected //During the compilation phase, Micronaut generates metadata about this class (using the micronaut-inject-java processor).
@Entity   // This annotation is handled by the JPA provider (like Hibernate).
@Table(name = "books")
@Data  //This is not directly read by Micronaut but is processed by Lombok at compile time.
public class Book {
    @Id  //Micronaut delegates this annotation’s handling to the JPA provider (e.g., Hibernate)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Micronaut’s JPA provider (e.g., Hibernate) uses this annotation.
    private Long id;

    private  String title;
    private String author;
    private  int quantity;
    private int publishedYear;
}
