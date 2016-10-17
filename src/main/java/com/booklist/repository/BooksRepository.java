package com.booklist.repository;

import com.booklist.domain.Books;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Books entity.
 */
@SuppressWarnings("unused")
public interface BooksRepository extends JpaRepository<Books,Long> {

}
