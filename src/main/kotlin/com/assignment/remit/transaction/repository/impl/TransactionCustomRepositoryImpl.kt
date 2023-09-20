package com.assignment.remit.transaction.repository.impl

import com.assignment.remit.transaction.Filter
import com.assignment.remit.transaction.Pagination
import com.assignment.remit.transaction.QTransaction.transaction
import com.assignment.remit.transaction.Transaction
import com.assignment.remit.transaction.model.TransactionFilterStatus
import com.assignment.remit.transaction.model.TransactionType
import com.assignment.remit.transaction.repository.TransactionCustomRepository
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory

class TransactionCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): TransactionCustomRepository {
    private fun eqAccountId(id: Long, status: TransactionFilterStatus?): BooleanExpression? {
        return when(status) {
            TransactionFilterStatus.INCOME ->
                transaction.depositAccountId.eq(id)
            TransactionFilterStatus.SPEND ->
                transaction.withdrawalAccountId.eq(id)
            null ->
                (transaction.withdrawalAccountId.eq(id).or(
                    transaction.depositAccountId.eq(id)
                ))
        }
    }

    private fun checkStatus(status: TransactionFilterStatus?): BooleanExpression? {
        return when(status) {
            TransactionFilterStatus.INCOME ->
                transaction.type.`in`(listOf(TransactionType.TRANSFER, TransactionType.DEPOSIT))
            TransactionFilterStatus.SPEND ->
                transaction.type.`in`(listOf(TransactionType.TRANSFER, TransactionType.WITHDRAW))
            null ->
                null
        }
    }

    private fun checkYearMonth(year: Int, month: Int): BooleanExpression? {
        return (
                transaction.createdAt.year().eq(year).and(
                    transaction.createdAt.month().eq(month)
                ))
    }

    private fun checkCursor(id: Long?): BooleanExpression? {
        return id?.let { transaction.id.lt(id) }
    }

    override fun findAllByFilter(filter: Filter, pagination: Pagination): List<Transaction> {
        return queryFactory.selectFrom(transaction)
            .where(
                eqAccountId(filter.accountId, filter.status),
                checkYearMonth(filter.year, filter.month),
                checkCursor(pagination.cursor),
                checkStatus(filter.status)
            )
            .orderBy(transaction.id.desc())
            .limit(pagination.limit)
            .fetch()
    }
}