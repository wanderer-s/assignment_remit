package com.assignment.remit.account.dto

import com.assignment.remit.transaction.model.TransactionType
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.util.*
import kotlin.properties.Delegates

data class DepositRequest(
    @field:Positive
    val amount: BigDecimal,
    val memo: String? = null
) {
    val uuid: UUID = UUID.randomUUID()
    val type = TransactionType.DEPOSIT
    var depositAccountId by Delegates.notNull<Long>()

    companion object {
        fun fromTransferRequest(request: TransferRequest): DepositRequest {
            val depositRequest = DepositRequest(
                request.amount,
                request.memo
            )

            depositRequest.depositAccountId = request.depositAccountId

            return depositRequest
        }
    }
}

data class WithdrawRequest(
    @field:Positive
    val amount: BigDecimal,
    val memo: String? = null,
) {
    val uuid: UUID = UUID.randomUUID()
    val type = TransactionType.WITHDRAW
    var withdrawAccountId by Delegates.notNull<Long>()
    var userId by Delegates.notNull<Long>()

    companion object {
        fun fromTransferRequest(request: TransferRequest): WithdrawRequest {
            val withdrawRequest = WithdrawRequest(
                request.amount,
                request.memo
                )

            withdrawRequest.withdrawAccountId = request.withdrawAccountId
            withdrawRequest.userId = request.userId

            return withdrawRequest
        }

    }
}

data class TransferRequest(
    @field:NotNull
    val depositAccountId: Long,
    @field:Positive
    val amount: BigDecimal,
    val memo: String? = null
) {
    val uuid: UUID = UUID.randomUUID()
    val type = TransactionType.TRANSFER
    var withdrawAccountId by Delegates.notNull<Long>()
    var userId by Delegates.notNull<Long>()
}