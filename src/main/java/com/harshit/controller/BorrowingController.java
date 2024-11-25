package com.harshit.controller;

import com.harshit.entity.Borrowing;
import com.harshit.exception.BookNotAvailableException;
import com.harshit.exception.EntityNotFoundException;
import com.harshit.service.BorrowingService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@Controller("/borrowing")
@Tag(name = "Borrowing Operations", description = "Operations related to borrowing and returning books")
public class BorrowingController {

    @Inject
    private BorrowingService borrowingService;

    @Operation(summary = "Borrow a book", description = "Allows a library member to borrow a book if it is available.")
    @ApiResponse(
            responseCode = "200",
            description = "Borrowing successful",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Book not available or Member not found",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Unexpected error",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))
    )
    @Post("/borrow")
    public HttpResponse<String> borrowBook(
            @Parameter(description = "Name of the book to be borrowed") @QueryValue String bookName,
            @Parameter(description = "Name of the member borrowing the book") @QueryValue String memberName) {
        try {
            Borrowing borrowing = borrowingService.borrowBook(bookName, memberName);
            return HttpResponse.ok("Borrowing successful.");
        } catch (EntityNotFoundException e) {
            return HttpResponse.badRequest(e.getMessage());
        } catch (BookNotAvailableException e) {
            return HttpResponse.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            return HttpResponse.serverError("An unexpected error occurred.");
        }
    }

    @Operation(summary = "Return a borrowed book", description = "Allows a library member to return a previously borrowed book.")
    @ApiResponse(
            responseCode = "200",
            description = "Book returned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Borrowing record not found",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))
    )
    @Post("/return")
    public HttpResponse<String> returnBook(
            @Parameter(description = "Name of the book to be returned") @QueryValue String bookName,
            @Parameter(description = "Name of the member returning the book") @QueryValue String memberName) {
        try {
            Borrowing borrowing = borrowingService.returnBook(bookName, memberName);
            return HttpResponse.ok("Book returned successfully by " + memberName);
        } catch (RuntimeException e) {
            return HttpResponse.badRequest("Borrowing record not found for the given book and member.");
        }
    }

    @Operation(summary = "Generate report of currently borrowed books")
    @ApiResponse(
            responseCode = "200",
            description = "Report generated successfully"
    )
    @Get("/report/currently-borrowed")
    public MutableHttpResponse<List<Object[]>> getCurrentlyBorrowedBooks(LocalDate currentDate) {
        List<Object[]> borrowedBooks = borrowingService.getCurrentlyBorrowedBooks(currentDate);
        return HttpResponse.ok(borrowedBooks);
    }

    @Operation(summary = "Generate report of overdue books")
    @ApiResponse(
            responseCode = "200",
            description = "Overdue report generated successfully"
    )
    @Get("/report/overdue")
    public MutableHttpResponse<List<Object[]>> getOverdueBooks() {
        List<Object[]> overdueBooks = borrowingService.getOverdueBooks();
        return HttpResponse.ok(overdueBooks);
    }
}
