package com.assignment.remit.transaction.repository

import com.assignment.remit.transaction.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository: JpaRepository<Transaction, Long>, TransactionCustomRepository