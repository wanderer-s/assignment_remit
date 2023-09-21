package com.assignment.remit.transaction

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/transaction")
@RestController
class TransactionController(
    private val transactionService: TransactionService
) {
    @GetMapping
    fun findAll(
        @ModelAttribute @Valid filter: Filter,
        @ModelAttribute pagination: Pagination
    ): List<TransactionResponse> {
        return transactionService.findAllByFilter(filter, pagination)
    }
}
