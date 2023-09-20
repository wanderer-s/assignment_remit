package com.assignment.remit.transaction.repository

import com.assignment.remit.transaction.Filter
import com.assignment.remit.transaction.Pagination
import com.assignment.remit.transaction.Transaction

interface TransactionCustomRepository {
    fun findAllByFilter(filter: Filter, pagination: Pagination): List<Transaction>
}