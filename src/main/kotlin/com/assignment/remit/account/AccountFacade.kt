package com.assignment.remit.account

import com.assignment.remit.account.dto.*
import com.assignment.remit.user.UserService
import jakarta.persistence.EntityNotFoundException
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

    fun getAccount(accountId: Long, userId: Long): AccountResponse {
        val foundAccount = accountService.get(accountId)
        if (foundAccount.user.id != userId) {
            throw EntityNotFoundException("계좌를 찾을 수 없습니다")
        }
        return AccountResponse.of(foundAccount)
    }
}
