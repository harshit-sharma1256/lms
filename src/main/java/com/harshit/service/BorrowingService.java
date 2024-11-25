package com.harshit.service;

import com.harshit.entity.Book;
import com.harshit.entity.Borrowing;
import com.harshit.entity.Member;
import com.harshit.exception.BookNotAvailableException;
import com.harshit.exception.EntityNotFoundException;
import com.harshit.repository.BookRepository;
import com.harshit.repository.BorrowingRepository;
import com.harshit.repository.MemberRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Singleton
public class BorrowingService {

    @Inject
    private BookRepository bookRepository;

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private BorrowingRepository borrowingRepository;

    @Transactional
    public Borrowing borrowBook(String bookName, String memberName) {
        // Fetch the book and member entities based on the provided names
        Optional<Book> bookOpt = bookRepository.findByTitle(bookName);
        Optional<Member> memberOpt = memberRepository.findByName(memberName);

        if (!bookOpt.isPresent() || !memberOpt.isPresent()) {
            throw new EntityNotFoundException("Either the book or member does not exist.");
        }

        Book book = bookOpt.get();
        Member member = memberOpt.get();

        if (book.getQuantity() > 1) {
            // Decrease the book quantity by 1
            book.setQuantity(book.getQuantity() - 1);

            // Create a new borrowing record
            Borrowing borrowing = new Borrowing();
            borrowing.setBorrowDate(LocalDate.now());
            borrowing.setMember(member);
            borrowing.setBook(book);
            borrowing.setReturnDate(LocalDate.now().plusDays(14)); // Assuming a 2-week borrowing period
            borrowing.setBookName(book.getTitle());
            borrowing.setMemberName(member.getName());

            // Save the updated book entity and borrowing record
            bookRepository.update(book);
            return borrowingRepository.save(borrowing);
        } else {
            throw new BookNotAvailableException("Sorry!! This book is currently not available for borrowing.");
        }
    }

    @Transactional
    public Borrowing returnBook(String bookName, String memberName) {
        // Find the borrowing record by book name and member name
        Optional<Borrowing> borrowingOpt = borrowingRepository.findByBookTitleAndMemberName(bookName, memberName);

        if (borrowingOpt.isPresent()) {
            Borrowing borrowing = borrowingOpt.get();
            Book book = borrowing.getBook();

            book.setQuantity(book.getQuantity() + 1);
            borrowing.setReturnDate(LocalDate.now());

            bookRepository.update(book);
            return borrowingRepository.update(borrowing);
        } else {
            throw new RuntimeException("No borrowing record found for the book: " + bookName + " and member: " + memberName);
        }
    }

    public List<Object[]> getCurrentlyBorrowedBooks(LocalDate currentDate) {
        return borrowingRepository.findCurrentlyBorrowedBooks(currentDate);
    }

    public List<Object[]> getOverdueBooks() {
        LocalDate currentDate = LocalDate.now();
        return borrowingRepository.findOverdueBooks(currentDate);
    }
}


