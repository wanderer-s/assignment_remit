package com.assignment.remit.transaction

import com.assignment.remit.transaction.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository
) {
    fun findAllByFilter(filter: Filter, pagination: Pagination): List<TransactionResponse> {
        val transactions = transactionRepository.findAllByFilter(filter, pagination)
        return transactions.map { TransactionResponse.of(it)}
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveHistory(request: TransactionRequest) {
        val transaction = Transaction(
            uuid = request.uuid,
            depositAccountId = request.depositAccountId,
            withdrawalAccountId = request.withdrawalAccountId,
            amount = request.amount,
            type = request.type,
            status = request.status,
            memo = request.memo
        )

        transactionRepository.save(transaction)
    }
}