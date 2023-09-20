package com.assignment.remit.transaction

import com.assignment.remit.transaction.model.TransactionFilterStatus
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

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
