package com.harshit.service;

import com.harshit.entity.Book;
import com.harshit.entity.Borrowing;
import com.harshit.entity.Member;
import com.harshit.exception.BookNotAvailableException;
import com.harshit.exception.EntityNotFoundException;
import com.harshit.repository.BookRepository;
import com.harshit.repository.BorrowingRepository;
import com.harshit.repository.MemberRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
class BorrowingServiceTest {

    @Inject
    BorrowingService borrowingService;

    @Inject
    BookRepository bookRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    BorrowingRepository borrowingRepository;

    @MockBean(BookRepository.class)
    BookRepository bookRepository() {
        return Mockito.mock(BookRepository.class);
    }

    @MockBean(MemberRepository.class)
    MemberRepository memberRepository() {
        return Mockito.mock(MemberRepository.class);
    }

    @MockBean(BorrowingRepository.class)
    BorrowingRepository borrowingRepository() {
        return Mockito.mock(BorrowingRepository.class);
    }

    @Test
    void testBorrowBookSuccessfully() {
        Book book = createDummyBook();
        Member member = createDummyMember();

        when(bookRepository.findByTitle("Micronaut Testing")).thenReturn(Optional.of(book));
        when(memberRepository.findByName("John Doe")).thenReturn(Optional.of(member));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Borrowing borrowing = borrowingService.borrowBook("Micronaut Testing", "John Doe");

        assertNotNull(borrowing, "Borrowing record should be created");
        assertEquals(4, book.getQuantity(), "Book quantity should decrease by 1");
        assertEquals(LocalDate.now().plusDays(14), borrowing.getReturnDate(), "Return date should be 2 weeks from now");

        verify(bookRepository, times(1)).update(book);
        verify(borrowingRepository, times(1)).save(any(Borrowing.class));
    }

    @Test
    void testBorrowBookNotAvailable() {
        Book book = createDummyBook();
        book.setQuantity(0);
        Member member = createDummyMember();

        when(bookRepository.findByTitle("Micronaut Testing")).thenReturn(Optional.of(book));
        when(memberRepository.findByName("John Doe")).thenReturn(Optional.of(member));

        assertThrows(BookNotAvailableException.class, () -> borrowingService.borrowBook("Micronaut Testing", "John Doe"), "Should throw exception when book is not available");
    }

    @Test
    void testBorrowBookEntityNotFound() {
        when(bookRepository.findByTitle("NonExistent")).thenReturn(Optional.empty());
        when(memberRepository.findByName("John Doe")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> borrowingService.borrowBook("NonExistent", "John Doe"), "Should throw exception when book or member is not found");
    }

    @Test
    void testReturnBookSuccessfully() {
        Book book = createDummyBook();
        Member member = createDummyMember();
        Borrowing borrowing = createDummyBorrowing(book, member);

        when(borrowingRepository.findByBookTitleAndMemberName("Micronaut Testing", "John Doe")).thenReturn(Optional.of(borrowing));
        when(borrowingRepository.update(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Borrowing returnedBorrowing = borrowingService.returnBook("Micronaut Testing", "John Doe");

        assertNotNull(returnedBorrowing, "Borrowing record should be updated on return");
        assertEquals(6, book.getQuantity(), "Book quantity should increase by 1");

        verify(bookRepository, times(1)).update(book);
        verify(borrowingRepository, times(1)).update(borrowing);
    }

    @Test
    void testReturnNonExistentBorrowingRecord() {
        when(borrowingRepository.findByBookTitleAndMemberName("NonExistent", "John Doe")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> borrowingService.returnBook("NonExistent", "John Doe"), "Should throw exception when borrowing record does not exist");
    }

    @Test
    void testGetCurrentlyBorrowedBooks() {
        when(borrowingRepository.findCurrentlyBorrowedBooks(any(LocalDate.class))).thenReturn(createDummyBorrowedBooks());

        List<Object[]> borrowedBooks = borrowingService.getCurrentlyBorrowedBooks(LocalDate.now());
        assertNotNull(borrowedBooks, "Currently borrowed books list should not be null");
        assertEquals(2, borrowedBooks.size(), "Should return 2 currently borrowed books");
    }

//    @Test
//    void testGetOverdueBooks() {
//        when(borrowingRepository.findOverdueBooks(any(LocalDate.class))).thenReturn(createDummyOverdueBooks());
//
//        List<Object[]> overdueBooks = borrowingService.getOverdueBooks();
//        assertNotNull(overdueBooks, "Overdue books list should not be null");
//        assertEquals(1, overdueBooks.size(), "Should return 1 overdue book");
//    }


    private Book createDummyBook() {
        Book book = new Book();
        book.setTitle("Micronaut Testing");
        book.setAuthor("Jane Doe");
        book.setQuantity(5);
        book.setPublishedYear(2021);
        return book;
    }

    private Member createDummyMember() {
        Member member = new Member();
        member.setName("John Doe");
        return member;
    }

    private Borrowing createDummyBorrowing(Book book, Member member) {
        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(LocalDate.now().minusDays(15));
        borrowing.setReturnDate(null); // Not returned yet
        return borrowing;
    }

    private List<Object[]> createDummyBorrowedBooks() {
        return List.of(
                new Object[]{"Micronaut Testing", "John Doe", LocalDate.now().minusDays(10), LocalDate.now().plusDays(4)},
                new Object[]{"Another Book", "Jane Smith", LocalDate.now().minusDays(5), LocalDate.now().plusDays(9)}
        );
    }

//    private List<Object[]> createDummyOverdueBooks() {
//        return List.of(
//                new Object[]{"Micronaut Testing", "John Doe", LocalDate.now().minusDays(30), LocalDate.now().minusDays(16)}
//        );
//    }

}
