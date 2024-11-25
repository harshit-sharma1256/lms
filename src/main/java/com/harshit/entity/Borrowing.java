package com.harshit.entity;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Introspected
@Entity
@Table(name = "borrowing")
@Data
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDate borrowDate;
    private LocalDate returnDate;

    private String bookName;
    private String memberName;
}
