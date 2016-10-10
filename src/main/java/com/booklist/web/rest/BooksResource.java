package com.booklist.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.booklist.domain.Books;

import com.booklist.repository.BooksRepository;
import com.booklist.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Books.
 */
@RestController
@RequestMapping("/api")
public class BooksResource {

    private final Logger log = LoggerFactory.getLogger(BooksResource.class);
        
    @Inject
    private BooksRepository booksRepository;

    /**
     * POST  /books : Create a new books.
     *
     * @param books the books to create
     * @return the ResponseEntity with status 201 (Created) and with body the new books, or with status 400 (Bad Request) if the books has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/books",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Books> createBooks(@Valid @RequestBody Books books) throws URISyntaxException {
        log.debug("REST request to save Books : {}", books);
        if (books.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("books", "idexists", "A new books cannot already have an ID")).body(null);
        }
        Books result = booksRepository.save(books);
        return ResponseEntity.created(new URI("/api/books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("books", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /books : Updates an existing books.
     *
     * @param books the books to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated books,
     * or with status 400 (Bad Request) if the books is not valid,
     * or with status 500 (Internal Server Error) if the books couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/books",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Books> updateBooks(@Valid @RequestBody Books books) throws URISyntaxException {
        log.debug("REST request to update Books : {}", books);
        if (books.getId() == null) {
            return createBooks(books);
        }
        Books result = booksRepository.save(books);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("books", books.getId().toString()))
            .body(result);
    }

    /**
     * GET  /books : get all the books.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of books in body
     */
    @RequestMapping(value = "/books",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Books> getAllBooks() {
        log.debug("REST request to get all Books");
        List<Books> books = booksRepository.findAll();
        return books;
    }

    /**
     * GET  /books/:id : get the "id" books.
     *
     * @param id the id of the books to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the books, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/books/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Books> getBooks(@PathVariable Long id) {
        log.debug("REST request to get Books : {}", id);
        Books books = booksRepository.findOne(id);
        return Optional.ofNullable(books)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /books/:id : delete the "id" books.
     *
     * @param id the id of the books to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/books/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBooks(@PathVariable Long id) {
        log.debug("REST request to delete Books : {}", id);
        booksRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("books", id.toString())).build();
    }

}
