package com.gatesma.jooq.service.impl;

import com.gatesma.jooq.service.BookService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Copyright (C), 2020
 * FileName: DefaultBookService
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/3/21 21:33
 * Description:
 */
public class DefaultBookService implements BookService {

    @Autowired
    DSLContext dsl;

    @Override
    @Transactional
    public void create(int id, int authorId, String title) {

        // This method has a "bug". It creates the same book twice. The second insert
        // should lead to a constraint violation, which should roll back the whole transaction
        for (int i = 0; i < 2; i++)
            dsl.insertInto(BOOK)
                    .set(BOOK.ID, id)
                    .set(BOOK.AUTHOR_ID, authorId)
                    .set(BOOK.TITLE, title)
                    .execute();
    }

}
