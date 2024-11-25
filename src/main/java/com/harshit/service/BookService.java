package com.harshit.service;

import com.harshit.entity.Book;
import com.harshit.repository.BookRepository;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.util.*;

@Singleton
public class BookService {

    @Inject
    BookRepository bookRepository;

    public Iterable<Book> getListOfBooks(){
        return bookRepository.findAll();
    }

    public HttpResponse<Book> getBookByName(String name) {
        return bookRepository.findByTitle(name)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Transactional
    public HttpResponse<String> saveBook(Book book) {
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            return HttpResponse.badRequest("!!! Book name is required !!!");
        }

        Optional<Book> existingBookOpt = bookRepository.findByTitle(book.getTitle());
        if (existingBookOpt.isPresent()) {
            return HttpResponse.badRequest("This book already exists. Please use the update operation.");
        }

        Book newBook = new Book();
        newBook.setTitle(book.getTitle());
        newBook.setAuthor(book.getAuthor() != null ? book.getAuthor() : "Unknown Author");
        if (book.getQuantity() == 0) {
            newBook.setQuantity(1);
            HttpResponse.ok("Warning, Quantity can't be 0, however, we are adding this with 1.");
        } else {
            newBook.setQuantity(book.getQuantity());
        }
        newBook.setPublishedYear(book.getPublishedYear());
        bookRepository.save(newBook);
        return HttpResponse.ok("Book added successfully!");
    }

    @Transactional
    public HttpResponse<String> updateBook(String name, Book book, boolean confirm) {
        Optional<Book> existingBookOpt = bookRepository.findByTitle(name);

        if (existingBookOpt.isPresent()) {
            Book existingBook = existingBookOpt.get();
            if (book.getTitle() != null) existingBook.setTitle(book.getTitle());
            if (book.getAuthor() != null) existingBook.setAuthor(book.getAuthor());
            if (book.getQuantity() > 0) existingBook.setQuantity(book.getQuantity());
            if (book.getPublishedYear() != 0) existingBook.setPublishedYear(book.getPublishedYear());

            bookRepository.update(existingBook);
            return HttpResponse.ok("Book updated successfully!");
        } else if (confirm) {
            return saveBook(book);
        } else {
            return HttpResponse.badRequest("This book doesn't exist. Set `confirm=true` to add it.");
        }
    }

    @Transactional
    public HttpResponse<String> deleteBook(String title) {
        Optional<Book> bk = bookRepository.findByTitle(title);
        if (bk.isPresent()) {
            bookRepository.deleteByTitle(title);
            return HttpResponse.ok("!!! Book deleted successfully !!!");
        } else {
            return HttpResponse.notFound("!!! The book you are trying to delete does not exist in the DB. !!!");
        }
    }

    public HttpResponse<Map<String, Object>> searchBooksByAuthor(String author) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (!author.matches("^[a-zA-Z0-9 ]+$")) {
            response.put("Warning", "!!! Only characters and numbers are recommended in the author's name !!!");
            author = author.replaceAll("[^a-zA-Z0-9 ]", "");
        }
        response.put("books", bookRepository.searchBooksByAuthor("%" + author + "%"));
        return HttpResponse.ok(response);
    }

    public HttpResponse<Map<String, Object>> searchBooksByTitle(String title) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (!title.matches("^[a-zA-Z0-9 ]+$")) {
            response.put("Warning", "!!! Only characters and numbers are recommended in the title !!!");
            title = title.replaceAll("[^a-zA-Z0-9 ]", "");
        }
        response.put("books", bookRepository.searchBooks("%" + title + "%"));
        return HttpResponse.ok(response);
    }

    public HttpResponse<Map<String, Object>> searchBooksByYearRange(int startYear, int endYear) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (startYear<  0 || endYear< 0) {
            response.put("Error", "!!! Year values cannot be negative !!!");
            return HttpResponse.badRequest(response);
        }
        if (endYear  < startYear) response.put("Warning", "!!! End year can't be less than start year !!!");

        response.put("books", bookRepository.searchBooksByYearRange(startYear, endYear));
        return HttpResponse.ok(response);
    }

    public HttpResponse<Map<String, Object>> searchBooksByTitleAndYearRange(String title, int startYear, int endYear) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (!title.matches("^[a-zA-Z0-9 ]+$")) {
            response.put("Warning", "!!! Only characters and numbers are recommended in the title !!!");
            title = title.replaceAll("[^a-zA-Z0-9 ]", "");
        }
        if (startYear < 0 || endYear < 0) {
            response.put("Error", "!!! Year values cannot be negative !!!");
            return HttpResponse.badRequest(response);
        }
        if (endYear < startYear) response.put("Warning", "!!! End year can't be less than start year !!!");

        response.put("books", bookRepository.searchBooksByTitleAndYearRange("%" + title + "%", startYear, endYear));
        return HttpResponse.ok(response);
    }
}
