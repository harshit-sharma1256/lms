package com.harshit.repository;

import com.harshit.entity.Book;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository  extends JpaRepository<Book,Long> {
    Optional<Book> findByTitle(String title);
    void deleteByTitle(String title);


    // Case-agnostic + Regular Expression Support + Similarity Matching
    @Query(value = """
    SELECT * FROM books
    WHERE 
        title ILIKE CONCAT('%', :title, '%')
        OR title % :title
        OR REGEXP_REPLACE(title, '[^a-zA-Z0-9]+', '', 'g') 
            ILIKE CONCAT('%', REGEXP_REPLACE(:title, '[^a-zA-Z0-9]+', '', 'g'), '%')
    ORDER BY similarity(title, :title) DESC
    """, nativeQuery = true)
    List<Book> searchBooks(String title);


    @Query(value = """
    SELECT * FROM books
    WHERE 
        author ILIKE CONCAT('%', :author, '%')
        OR author % :author
        OR REGEXP_REPLACE(author, '[^a-zA-Z0-9]+', '', 'g')
            ILIKE CONCAT('%', REGEXP_REPLACE(:author, '[^a-zA-Z0-9]+', '', 'g'), '%')
    ORDER BY similarity(author, :author) DESC
        """, nativeQuery = true)
    List<Book> searchBooksByAuthor(String author);

    @Query(value = """
    SELECT * FROM books
    WHERE published_year BETWEEN :startYear AND :endYear
    """, nativeQuery = true)
    List<Book> searchBooksByYearRange(int startYear, int endYear);

    @Query(value = """
    SELECT * FROM books
    WHERE 
        (REGEXP_REPLACE(LOWER(title), '[^a-zA-Z0-9]+', '', 'g') 
            ILIKE CONCAT('%', REGEXP_REPLACE(LOWER(:title), '[^a-zA-Z0-9]+', '', 'g'), '%')
         OR title % :title)
        AND published_year BETWEEN :startYear AND :endYear
    ORDER BY similarity(title, :title) DESC
    """, nativeQuery = true)
    List<Book> searchBooksByTitleAndYearRange(String title, int startYear, int endYear);

}
