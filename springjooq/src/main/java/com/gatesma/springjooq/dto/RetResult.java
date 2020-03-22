package com.gatesma.springjooq.dto;

import com.gatesma.springjooq.enums.RetCode;

/**
 * Copyright (C), 2020
 * FileName: RetResult
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/3/22 10:08
 * Description:
 */
public class RetResult<T> {

    public int code;

    private String msg;

    private T data;

    public RetResult<T> setCode(RetCode retCode) {
        this.code = retCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public RetResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RetResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public RetResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public RetResult() {
    }

    public RetResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public RetResult(RetCode retCode, String msg, T data) {
        this.code = retCode.code;
        this.msg = msg;
        this.data = data;
    }
}