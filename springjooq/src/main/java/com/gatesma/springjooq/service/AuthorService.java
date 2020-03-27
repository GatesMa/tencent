package com.gatesma.springjooq.service;

import com.gatesma.springjooq.jooq.tables.pojos.Author;
import com.gatesma.springjooq.jooq.tables.pojos.Book;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Copyright (C), 2020
 * FileName: AuthorService
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/3/25 12:49
 * Description:
 */
public interface AuthorService {

    @Transactional
    Author create(Author author);

    List<Author> list();

    List<Author> getAuthorById(Integer id);

    List<Author> delAuthorById(Integer id);

}
