package com.gatesma.jooq;

import org.jooq.Transaction;
import org.springframework.transaction.TransactionStatus;

/**
 * Copyright (C), 2020
 * FileName: SpringTransaction
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/3/21 21:36
 * Description:
 */

class SpringTransaction implements Transaction {
    final TransactionStatus tx;

    SpringTransaction(TransactionStatus tx) {
        this.tx = tx;
    }
}