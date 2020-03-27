package com.gatesma.springjooq.controller;

import com.gatesma.springjooq.dto.RetResult;
import com.gatesma.springjooq.enums.RetCode;
import com.gatesma.springjooq.jooq.tables.pojos.Book;
import com.gatesma.springjooq.service.BookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Copyright (C), 2020
 * FileName: JooqController
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/3/21 22:11
 * Description:
 */
@RestController
public class BookController {

    @Resource
    private BookService bookService;

    @GetMapping(value = "/book")
    public RetResult<List<Book>> list() {

        return new RetResult<List<Book>>(RetCode.SUCCESS, "查询数据库成功 - Book 列表", bookService.list());
    }

    @GetMapping(value = "/book/{id}")
    public RetResult getBookById(@PathVariable("id") Integer id) {

        List<Book> books = bookService.getBookById(id);
        if(books.size() == 0) {
            return new RetResult(RetCode.SUCCESS, "查询数据库成功 - 没有这条数据", null);
        } else if(books.size() == 1) {
            return new RetResult(RetCode.SUCCESS, "查询数据库成功 - book by id -----> " + id, books.get(0));
        } else {
            return new RetResult(RetCode.SUCCESS, "查询数据库成功 - 这个Id对应数据库多条记录，可能出现问题，请检查, id -----> " + id, books);
        }
    }

    @PostMapping(value = "/book")
    public void create(Book book) {
        bookService.create(book);
    }

    @DeleteMapping(value = "/book/{id}")
    public RetResult delById(@PathVariable("id") Integer id) {
        List<Book> books = bookService.delBookById(id);
        if(books.size() == 0) {
            return new RetResult(RetCode.SUCCESS, "删除数据记录 - 没有这条数据， id ----> " + id, null);
        } else if(books.size() == 1) {
            return new RetResult(RetCode.SUCCESS, "删除数据成功 - book by id -----> " + id, books.get(0));
        } else {
            return new RetResult(RetCode.SUCCESS, "删除数据成功 - 这个Id对应数据库多条记录，可能出现问题，请检查, id -----> " + id, books);
        }
    }



}
