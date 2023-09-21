package com.assignment.remit.account

import com.assignment.remit.account.dto.*
import com.assignment.remit.user.UserService
import org.springframework.stereotype.Service

@Service
class AccountFacade(
    private val userService: UserService,
    private val accountService: AccountService
) {

    fun createAccount(request: CreateAccountRequest): AccountResponse {
        val foundUser = userService.get(request.userId)

        val newAccount = accountService.create(foundUser, request.depositLimit)
        return AccountResponse.of(newAccount)
    }

    fun deposit(request: DepositRequest) {
        accountService.deposit(request)
    }

    fun withdraw(request: WithdrawRequest) {
        accountService.withdraw(request)
    }

    fun transfer(request: TransferRequest) {
        accountService.transfer(request)
    }
}
