package com.assignment.remit.transaction

import com.assignment.remit.global.BaseEntity
import com.assignment.remit.transaction.model.TransactionStatus
import com.assignment.remit.transaction.model.TransactionType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.math.BigDecimal
import java.util.UUID

@Entity
class Transaction(
    val uuid: UUID = UUID.randomUUID(),
    val depositAccountId: Long? = null,
    val withdrawalAccountId: Long? = null,
    val amount: BigDecimal,
    val memo: String? = null,
    @Enumerated(EnumType.STRING)
    val status: TransactionStatus = TransactionStatus.PENDING,
    @Enumerated(EnumType.STRING)
    val type: TransactionType
): BaseEntity<Transaction>()