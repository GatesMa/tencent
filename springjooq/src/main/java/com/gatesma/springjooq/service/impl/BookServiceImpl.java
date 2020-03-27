package com.gatesma.springjooq.service.impl;

import com.gatesma.springjooq.jooq.tables.pojos.Book;
import com.gatesma.springjooq.jooq.tables.records.BookRecord;
import com.gatesma.springjooq.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gatesma.springjooq.jooq.Tables.BOOK;


/**
 * Copyright (C), 2020
 * FileName: BookServiceImpl
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/3/21 22:21
 * Description:
 */
@Service
@Slf4j
public class BookServiceImpl implements BookService {

    @Autowired
    DSLContext dsl;

    @Override
    @Transactional
    public Book create(Book book) {
        // This method has a "bug". It creates the same book twice. The second insert
        // should lead to a constraint violation, which should roll back the whole transaction

        int execute = dsl.insertInto(BOOK)
                .set(BOOK.AUTHOR_ID, book.getAuthorId())
                .set(BOOK.TITLE, book.getTitle())
                .set(BOOK.PUBLISHED_IN, book.getPublishedIn())
                .set(BOOK.LANGUAGE_ID, book.getLanguageId())
                .execute();
        book.setId(execute);
        return book;
    }

    @Override
    public List<Book> list() {
        List<Book> books = dsl.select()
                .from(BOOK)
                .fetchInto(Book.class);
        return books;
    }

    @Override
    public List<Book> getBookById(Integer id) {
        List<Book> books = dsl.select()
                .from(BOOK)
                .where(BOOK.ID.eq(id))
                .fetchInto(Book.class);
        return books;
    }

    @Override
    public List<Book> delBookById(Integer id) {
        List<Book> books = getBookById(id);
        dsl.delete(BOOK).where(BOOK.ID.eq(id)).execute();

        return books;
    }
}
