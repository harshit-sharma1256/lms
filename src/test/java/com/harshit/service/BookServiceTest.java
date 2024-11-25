package com.harshit.service;

import com.harshit.entity.Book;
import com.harshit.repository.BookRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
class BookServiceTest {

    @Inject
    BookService bookService;

    @Inject
    BookRepository bookRepository;

    @MockBean(BookRepository.class)
    BookRepository bookRepository() {
        return Mockito.mock(BookRepository.class);
    }

    @Test
    void testGetListOfBooks() {
        when(bookRepository.findAll()).thenReturn(createDummyBooks());
        Iterable<Book> books = bookService.getListOfBooks();
        assertNotNull(books, "List of books should not be null");
    }
//
//    @Test
//    void testGetBookById() {
//        Book dummyBook = createDummyBook();
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(dummyBook));
//
//        Optional<Book> foundBook = bookService.get(1L);
//        assertTrue(foundBook.isPresent(), "Book should be present for ID: 1");
//        assertEquals(dummyBook.getTitle(), foundBook.get().getTitle(), "Book title should match");
//    }

    @Test
    void testGetBookByName() {
        Book dummyBook = createDummyBook();
        when(bookRepository.findByTitle("Micronaut Testing")).thenReturn(Optional.of(dummyBook));

        Optional<Book> foundBook = bookService.getBookByName("Micronaut Testing").getBody();
        assertTrue(foundBook.isPresent(), "Book should be found for the given title");
        assertEquals(dummyBook.getAuthor(), foundBook.get().getAuthor(), "Author should match");
    }

    @Test
    void testSaveBookWithEmptyTitle() {
        Book newBook = new Book();
        HttpResponse<String> response = bookService.saveBook(newBook);
        assertEquals(HttpResponse.badRequest("!!! Book name is required !!!").getStatus(), response.getStatus(), "Should return bad request if title is empty");
    }

    @Test
    void testSaveBook() {
        Book newBook = createDummyBook();
        when(bookRepository.findByTitle(newBook.getTitle())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        HttpResponse<String> response = bookService.saveBook(newBook);
        assertEquals(HttpResponse.ok("Book added successfully!").getStatus(), response.getStatus(), "Book should be added successfully");
    }

    @Test
    void testSaveExistingBook() {
        Book existingBook = createDummyBook();
        when(bookRepository.findByTitle(existingBook.getTitle())).thenReturn(Optional.of(existingBook));

        HttpResponse<String> response = bookService.saveBook(existingBook);
        assertEquals(HttpResponse.badRequest("This book already exists. Please use the update operation.").getStatus(), response.getStatus(), "Should return bad request for existing book");
    }

    @Test
    void testUpdateExistingBook() {
        Book existingBook = createDummyBook();
        when(bookRepository.findByTitle("Micronaut Testing")).thenReturn(Optional.of(existingBook));

        Book updatedBook = new Book();
        updatedBook.setTitle("Micronaut Updated Testing");
        updatedBook.setAuthor("Jane Updated");
        updatedBook.setQuantity(10);
        updatedBook.setPublishedYear(2022);

        HttpResponse<String> response = bookService.updateBook("Micronaut Testing", updatedBook, false);
        assertEquals(HttpResponse.ok("Book updated successfully!").getStatus(), response.getStatus(), "Book should be updated successfully");

        verify(bookRepository, times(1)).update(any(Book.class));
    }

    @Test
    void testUpdateNonExistentBookWithConfirm() {
        when(bookRepository.findByTitle("NonExistent")).thenReturn(Optional.empty());

        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("New Author");

        HttpResponse<String> response = bookService.updateBook("NonExistent", newBook, true);
        assertEquals(HttpResponse.ok("Book didn't exist, but has now been added successfully!").getStatus(), response.getStatus(), "New book should be added with confirm=true");
    }

    @Test
    void testUpdateNonExistentBookWithoutConfirm() {
        when(bookRepository.findByTitle("NonExistent")).thenReturn(Optional.empty());

        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("New Author");

        HttpResponse<String> response = bookService.updateBook("NonExistent", newBook, false);
        assertEquals(HttpResponse.badRequest("This book doesn't exist. Do you want to add another book with these values? Set `confirm=true` in the query parameter to add it.").getStatus(), response.getStatus(), "Should return bad request without confirm");
    }

    @Test
    void testDeleteBook() {
        Book existingBook = createDummyBook();
        when(bookRepository.findByTitle("Micronaut Testing")).thenReturn(Optional.of(existingBook));

        bookService.deleteBook("Micronaut Testing");
        verify(bookRepository, times(1)).deleteByTitle("Micronaut Testing");
    }

    @Test
    void testDeleteNonExistentBook() {
        // Arrange: Set up the repository to return empty when the book title is not found
        when(bookRepository.findByTitle("NonExistent")).thenReturn(Optional.empty());

        HttpResponse<String> response = bookService.deleteBook("NonExistent");

        assertEquals(HttpResponse.notFound("!!! The book you are trying to delete does not exist in the DB. !!!").getStatus(), response.getStatus(), "Should return not found for non-existent book");
        assertEquals("!!! The book you are trying to delete does not exist in the DB. !!!", response.body(), "Error message should match");
    }


    @Test
    void testSearchBooksByAuthor() {
        when(bookRepository.searchBooksByAuthor("%Jane%")).thenReturn(createDummyBooks());

        HttpResponse<Map<String, Object>> response = bookService.searchBooksByAuthor("Jane");
        assertEquals(2, ((List<?>) response.body().get("books")).size(), "Should find books matching the author");

        response = bookService.searchBooksByAuthor("Jane!@#");
        assertTrue(Objects.requireNonNull(response.body()).containsKey("Warning"), "Should contain warning for invalid characters");
    }

    @Test
    void testSearchBooksByTitle() {
        when(bookRepository.searchBooks("%Micronaut%")).thenReturn(createDummyBooks());

        HttpResponse<Map<String, Object>> response = bookService.searchBooksByTitle("Micronaut");
        assertEquals(2, ((List<?>) Objects.requireNonNull(response.body()).get("books")).size(), "Should find books matching the title");

        response = bookService.searchBooksByTitle("Micronaut!@#");
        assertTrue(Objects.requireNonNull(response.body()).containsKey("Warning"), "Should contain warning for invalid characters");
    }


    @Test
    void testSearchBooksByYearRange() {
        when(bookRepository.searchBooksByYearRange(2000, 2022)).thenReturn(createDummyBooks());

        HttpResponse<Map<String, Object>> response = bookService.searchBooksByYearRange(2000, 2022);
        assertEquals(2, ((List<?>) response.body().get("books")).size(), "Should find books within the year range");

        response = bookService.searchBooksByYearRange(-2000, 2022);
        assertTrue(Objects.requireNonNull(response.body()).containsKey("Error"), "Should contain error for negative years");
    }

    @Test
    void testSearchBooksByTitleAndYearRange() {
        when(bookRepository.searchBooksByTitleAndYearRange("%Micronaut%", 2000, 2022)).thenReturn(createDummyBooks());

        HttpResponse<Map<String, Object>> response = bookService.searchBooksByTitleAndYearRange("Micronaut", 2000, 2022);
        assertEquals(2, ((List<?>) response.body().get("books")).size(), "Should find books with title and within year range");

        // Test special characters and invalid year range
        response = bookService.searchBooksByTitleAndYearRange("Micronaut!@#", 2022, 2000);
        assertTrue(response.body().containsKey("Warning"), "Should contain warning for special characters or invalid year range");
    }
    private Book createDummyBook() {
        Book book = new Book();
        book.setTitle("Micronaut Testing");
        book.setAuthor("Jane Doe");
        book.setQuantity(5);
        book.setPublishedYear(2021);
        return book;
    }

    private List<Book> createDummyBooks() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setQuantity(3);
        book1.setPublishedYear(2005);

        Book book2 = new Book();
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setQuantity(2);
        book2.setPublishedYear(2010);

        return List.of(book1, book2);
    }
}
