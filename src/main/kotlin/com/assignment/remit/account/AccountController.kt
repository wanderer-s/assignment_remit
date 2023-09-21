package com.assignment.remit.account

import com.assignment.remit.account.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/account")
class AccountController(
    private val accountFacade: AccountFacade
) {
    @GetMapping("{id}/user/{userId}")
    fun get(
        @PathVariable id: Long,
        @PathVariable userId: Long
    ): AccountResponse {
        return accountFacade.getAccount(id, userId)
    }

    @PostMapping("user/{userId}")
    fun create(
        @RequestBody @Valid request: CreateAccountRequest,
        @PathVariable userId: Long,
    ): ResponseEntity<AccountResponse> {
        request.userId = userId
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(accountFacade.createAccount(request))
    }

    @PatchMapping("{id}/deposit")
    fun deposit(
        @PathVariable id: Long,
        @RequestBody @Valid request: DepositRequest
    ): ResponseEntity<Unit> {
        request.depositAccountId = id
        accountFacade.deposit(request)

        return ResponseEntity.ok().build()
    }

    @PatchMapping("{id}/withdraw/user/{userId}")
    fun withdraw(
        @PathVariable id: Long,
        @RequestBody @Valid request: WithdrawRequest,
        @PathVariable userId: Long
    ): ResponseEntity<Unit> {
        request.withdrawAccountId = id
        request.userId = userId

        accountFacade.withdraw(request)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("{id}/transfer/user/{userId}")
    fun transfer(
        @PathVariable id: Long,
        @RequestBody @Valid request: TransferRequest,
        @PathVariable userId: Long
    ): ResponseEntity<Unit> {
        request.withdrawAccountId = id
        request.userId = userId

        accountFacade.transfer(request)
        return ResponseEntity.ok().build()
    }
}