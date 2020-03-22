package com.gatesma.jooq.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * Copyright (C), 2020
 * FileName: BookService
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/3/21 21:33
 * Description:
 */
public interface BookService {
    /**
     * Create a new book.
     * <p>
     * The implementation of this method has a bug, which causes this method to
     * fail and roll back the transaction.
     */
    @Transactional
    void create(int id, int authorId, String title);
}
