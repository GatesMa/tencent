package com.gatesma.springjooq.service;

/**
 * Copyright (C), 2020
 * FileName: BookService
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/3/21 22:21
 * Description:
 */

import com.gatesma.springjooq.jooq.tables.pojos.Book;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface BookService {

    /**
     * Create a new book.
     * <p>
     * The implementation of this method has a bug, which causes this method to
     * fail and roll back the transaction.
     */
    @Transactional
    void create(Book book);

    List<Book> list();

    List<Book> getBookById(Integer id);

}
