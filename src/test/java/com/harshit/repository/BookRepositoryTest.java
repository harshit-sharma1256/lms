package com.harshit.repository;

import com.harshit.entity.Book;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class BookRepositoryTest {

    @Inject
    BookRepository bookRepository;

    @Test
    void testBookRepositoryOperations() {
        // Step 1: Create and save a new Book entity
        Book book = new Book();
        book.setTitle("Scary nights");
        book.setAuthor("Harshit sharma");
        book.setQuantity(6);
        book = bookRepository.save(book);

        // Step 2: Retrieve the book by title
        Optional<Book> retrievedBook = bookRepository.findByTitle("Scary nights");
        assertTrue(retrievedBook.isPresent(), "Book should be present in the repository");
        assertEquals("Harshit sharma", retrievedBook.get().getAuthor(), "Author name should match");
        assertEquals(6, retrievedBook.get().getQuantity(), "Quantity should match");

        // Step 3: Delete the book by title
        bookRepository.deleteByTitle("Scary nights");

        // Step 4: Verify the book is no longer in the repository
        Optional<Book> deletedBook = bookRepository.findByTitle("Scary nights");
        assertFalse(deletedBook.isPresent(), "Book should be deleted from the repository");
    }
}
