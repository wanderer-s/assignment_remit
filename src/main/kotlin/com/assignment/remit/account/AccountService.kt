package com.assignment.remit.account

import com.assignment.remit.account.dto.DepositRequest
import com.assignment.remit.account.dto.TransferRequest
import com.assignment.remit.account.dto.WithdrawRequest
import com.assignment.remit.account.repository.AccountRepository
import com.assignment.remit.global.CustomException
import com.assignment.remit.transaction.TransactionRequest
import com.assignment.remit.transaction.TransactionService
import com.assignment.remit.transaction.model.TransactionStatus
import com.assignment.remit.user.User
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val transactionService: TransactionService
) {
    private fun get(id: Long): Account {
        return accountRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("계좌를 찾을 수 없습니다")
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun deposit(request: DepositRequest, saveHistory: Boolean = true) {
        val account = get(request.depositAccountId)
        val transactionRequest = TransactionRequest.fromDeposit(request)

        try {
            account.deposit(request.amount)
            transactionRequest.status = TransactionStatus.SUCCESS
        } catch (exception: CustomException) {
            transactionRequest.status = TransactionStatus.FAIL
            transactionRequest.memo = exception.message
            throw exception
        } finally {
            if (saveHistory) {
                transactionService.saveHistory(transactionRequest)
            }
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun withdraw(request: WithdrawRequest, saveHistory: Boolean = true) {
        val account = get(request.withdrawAccountId)
        val transactionRequest = TransactionRequest.fromWithdraw(request)

        try {
            account.withdraw(request.userId, request.amount)
            transactionRequest.status = TransactionStatus.SUCCESS
        } catch (exception: CustomException) {
            transactionRequest.status = TransactionStatus.FAIL
            transactionRequest.memo = exception.message
            throw exception
        } finally {
            if (saveHistory) {
                transactionService.saveHistory(transactionRequest)
            }
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun transfer(request: TransferRequest) {
        val transactionRequest = TransactionRequest.fromTransfer(request)

        try {
            val withdrawRequest = WithdrawRequest.fromTransferRequest(request)
            withdraw(withdrawRequest, false)

            val depositRequest = DepositRequest.fromTransferRequest(request)
            deposit(depositRequest, false)

            transactionRequest.status = TransactionStatus.SUCCESS
        } catch(exception: CustomException) {
            transactionRequest.status = TransactionStatus.FAIL
            transactionRequest.memo = exception.message
            throw exception
        } finally {
            transactionService.saveHistory(transactionRequest)
        }
    }

    fun create(user: User, depositLimit: BigDecimal): Account {
        val account = Account(user, depositLimit)
        return accountRepository.save(account)
    }
}