package com.assignment.remit.transaction

import com.assignment.remit.transaction.model.TransactionStatus
import com.assignment.remit.transaction.model.TransactionType
import java.math.BigDecimal

fun createTransaction(
    depositAccountId: Long? = null,
    withdrawalAccountId: Long? = null,
    type: TransactionType = TransactionType.TRANSFER,
    status: TransactionStatus = TransactionStatus.SUCCESS
) =
    Transaction(
        depositAccountId = depositAccountId,
        withdrawalAccountId = withdrawalAccountId,
        amount = BigDecimal(10000),
        type = type,
        status = status
    )