package com.booklist.web.rest;

import com.booklist.BookListApp;

import com.booklist.domain.Books;
import com.booklist.repository.BooksRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BooksResource REST controller.
 *
 * @see BooksResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookListApp.class)
public class BooksResourceIntTest {

    private static final String DEFAULT_TITLE = "AAA";
    private static final String UPDATED_TITLE = "BBB";

    private static final String DEFAULT_AUTHOR = "AAA";
    private static final String UPDATED_AUTHOR = "BBB";

    @Inject
    private BooksRepository booksRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restBooksMockMvc;

    private Books books;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BooksResource booksResource = new BooksResource();
        ReflectionTestUtils.setField(booksResource, "booksRepository", booksRepository);
        this.restBooksMockMvc = MockMvcBuilders.standaloneSetup(booksResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Books createEntity(EntityManager em) {
        Books books = new Books()
                .title(DEFAULT_TITLE)
                .author(DEFAULT_AUTHOR);
        return books;
    }

    @Before
    public void initTest() {
        books = createEntity(em);
    }

    @Test
    @Transactional
    public void createBooks() throws Exception {
        int databaseSizeBeforeCreate = booksRepository.findAll().size();

        // Create the Books

        restBooksMockMvc.perform(post("/api/books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(books)))
                .andExpect(status().isCreated());

        // Validate the Books in the database
        List<Books> books = booksRepository.findAll();
        assertThat(books).hasSize(databaseSizeBeforeCreate + 1);
        Books testBooks = books.get(books.size() - 1);
        assertThat(testBooks.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBooks.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = booksRepository.findAll().size();
        // set the field null
        books.setTitle(null);

        // Create the Books, which fails.

        restBooksMockMvc.perform(post("/api/books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(books)))
                .andExpect(status().isBadRequest());

        List<Books> books = booksRepository.findAll();
        assertThat(books).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAuthorIsRequired() throws Exception {
        int databaseSizeBeforeTest = booksRepository.findAll().size();
        // set the field null
        books.setAuthor(null);

        // Create the Books, which fails.

        restBooksMockMvc.perform(post("/api/books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(books)))
                .andExpect(status().isBadRequest());

        List<Books> books = booksRepository.findAll();
        assertThat(books).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the books
        restBooksMockMvc.perform(get("/api/books?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR.toString())));
    }

    @Test
    @Transactional
    public void getBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get the books
        restBooksMockMvc.perform(get("/api/books/{id}", books.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(books.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBooks() throws Exception {
        // Get the books
        restBooksMockMvc.perform(get("/api/books/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Update the books
        Books updatedBooks = booksRepository.findOne(books.getId());
        updatedBooks
                .title(UPDATED_TITLE)
                .author(UPDATED_AUTHOR);

        restBooksMockMvc.perform(put("/api/books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBooks)))
                .andExpect(status().isOk());

        // Validate the Books in the database
        List<Books> books = booksRepository.findAll();
        assertThat(books).hasSize(databaseSizeBeforeUpdate);
        Books testBooks = books.get(books.size() - 1);
        assertThat(testBooks.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBooks.getAuthor()).isEqualTo(UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    public void deleteBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);
        int databaseSizeBeforeDelete = booksRepository.findAll().size();

        // Get the books
        restBooksMockMvc.perform(delete("/api/books/{id}", books.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Books> books = booksRepository.findAll();
        assertThat(books).hasSize(databaseSizeBeforeDelete - 1);
    }
}
