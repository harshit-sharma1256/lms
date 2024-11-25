package com.harshit.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testBookEntity() {
        Book book = new Book();

        book.setTitle("");
        book.setAuthor("");
        book.setQuantity(0);

        // Validate that the fields are correctly set
        assertEquals("", book.getTitle());
        assertEquals("", book.getAuthor());
        assertEquals(0, book.getQuantity());

        // Validate initial ID is null (since it is set by the database)
        assertNull(book.getId());
    }
}
