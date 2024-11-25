package com.harshit.controller;

import com.harshit.entity.Book;
import com.harshit.service.BookService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;

import java.util.Map;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller(value = "/books")
@Tag(name = "Book Management", description = "APIs related to managing books in the library")
public class BookController {

    @Inject
    BookService bookService;

    @Operation(summary = "Check the health of the Book API", description = "Returns a simple status message")
    @Get(value = "/health", produces = APPLICATION_JSON)
    public String health() {
        return "OK";
    }

    @Operation(summary = "Retrieve all books", description = "Fetches all books from the library")
    @Get(value = "/getAll")
    public Iterable<Book> getAllBooks() {
        return bookService.getListOfBooks();
    }

    @Operation(summary = "Fetch a specific book by name", description = "Retrieves a book by its title")
    @Get(value = "/{name}")
    public HttpResponse<Book> getSpecificBook(@PathVariable("name") String title) {
        return bookService.getBookByName(title);
    }

    @Operation(summary = "Add a new book to the library", description = "Creates a new book record")
    @Post("/add")
    public HttpResponse<String> addAnotherBook(@Body Book book) {
        return bookService.saveBook(book);
    }

    @Operation(summary = "Update an existing book", description = "Updates the information of an existing book")
    @Put("/update/{name}")
    public HttpResponse<String> updateBook(@PathVariable("name") String title, @Body Book book, @QueryValue(defaultValue = "false") boolean confirm) {
        return bookService.updateBook(title, book, confirm);
    }

    @Operation(summary = "Delete a book by name", description = "Removes a book from the library by its title")
    @Delete("/del/{name}")
    public HttpResponse<String> deleteBook(@PathVariable("name") String title) {
        return bookService.deleteBook(title);
    }

    @Operation(summary = "Search books by author name", description = "Fetches all books that match the given author name")
    @Get("/search/author/{author}")
    public HttpResponse<Map<String, Object>> searchBooksByAuthor(@PathVariable("author") String author) {
        return bookService.searchBooksByAuthor(author);
    }

    @Operation(summary = "Search books by title", description = "Fetches books that match the given title, with warning for invalid input.")
    @Get("/search/title/{title}")
    public HttpResponse<Map<String, Object>> searchBooksByTitle(@PathVariable("title") String title) {
        return bookService.searchBooksByTitle(title);
    }

    @Operation(summary = "Search books by year range", description = "Fetches all books published within the given start and end year")
    @Get("/search/yearRange")
    public HttpResponse<Map<String, Object>> searchBooksByYearRange(@QueryValue int startYear, @QueryValue int endYear) {
        return bookService.searchBooksByYearRange(startYear, endYear);
    }

    @Operation(summary = "Search books by title and year range", description = "Fetches all books that match the title and fall within the given year range")
    @Get("/search/titleYearRange")
    public HttpResponse<Map<String, Object>> searchBooksByTitleAndYearRange(@QueryValue String title, @QueryValue int startYear, @QueryValue int endYear) {
        return bookService.searchBooksByTitleAndYearRange(title, startYear, endYear);
    }
}
