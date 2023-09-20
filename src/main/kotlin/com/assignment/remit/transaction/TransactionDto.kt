package com.assignment.remit.transaction

import com.assignment.remit.transaction.model.TransactionFilterStatus
import com.assignment.remit.transaction.model.TransactionStatus
import com.assignment.remit.transaction.model.TransactionType
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class TransactionRequest(
    val uuid: UUID,
    val depositAccountId: Long? = null,
    val withdrawalAccountId: Long? = null,
    val amount: BigDecimal,
    val type: TransactionType,
    var memo: String? = null
) {
    var status = TransactionStatus.PENDING
}

data class TransactionResponse(
    val id: Long,
    val uuid: UUID,
    val withdrawalAccountId: Long? = null,
    val depositAccountId: Long? = null,
    val amount: BigDecimal,
    val type: TransactionType,
    val status: TransactionStatus,
    val memo: String? = null,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(transaction: Transaction) =
            TransactionResponse(
                transaction.id,
                transaction.uuid,
                transaction.withdrawalAccountId,
                transaction.depositAccountId,
                transaction.amount,
                transaction.type,
                transaction.status,
                transaction.memo,
                transaction.createdAt,
            )
    }
}

data class Filter(
    val year: Int = LocalDate.now().year,
    @field:Min(1)
    @field:Max(12)
    val month: Int = LocalDate.now().monthValue,
    @field:NotNull
    val accountId: Long,
    val status: TransactionFilterStatus? = null
)

data class Pagination(
    val limit : Long = 10,
    val cursor: Long?
)
