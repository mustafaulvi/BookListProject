package com.booklist.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Books.
 */
@Entity
@Table(name = "books")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Books implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 3, max = 200)
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @NotNull
    @Size(min = 3, max = 25)
    @Column(name = "author", length = 25, nullable = false)
    private String author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Books title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public Books author(String author) {
        this.author = author;
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Books books = (Books) o;
        if(books.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, books.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Books{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", author='" + author + "'" +
            '}';
    }
}
