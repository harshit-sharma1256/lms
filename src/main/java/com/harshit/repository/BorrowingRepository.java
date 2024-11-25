package com.harshit.repository;
import com.harshit.entity.Borrowing;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends CrudRepository<Borrowing,Long> {
     Optional<Borrowing> findByBookTitle(String bookTitle) ;
     Optional<Borrowing> findByBookTitleAndMemberName(String bookTitle, String memberName);
     // Find books that are currently borrowed (return_date is in the future)
     @Query(nativeQuery = true, value = "SELECT b.title,br.member_name FROM borrowing br JOIN books b ON br.book_id = b.id WHERE br.return_date > :currentDate")
     List<Object[]> findCurrentlyBorrowedBooks(LocalDate currentDate);

     // Find books that are overdue (return_date is in the past)
     @Query(nativeQuery = true, value = "SELECT b.title,br.member_name FROM borrowing br JOIN books b ON br.book_id = b.id WHERE br.return_date  < :currentDate")
     List<Object[]> findOverdueBooks(LocalDate currentDate);
}
