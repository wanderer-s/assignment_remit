package com.assignment.remit.account

import com.assignment.remit.account.dto.DepositRequest
import com.assignment.remit.account.dto.TransferRequest
import com.assignment.remit.account.dto.WithdrawRequest
import com.assignment.remit.account.repository.AccountRepository
import com.assignment.remit.global.CustomException
import com.assignment.remit.transaction.TransactionRequest
import com.assignment.remit.transaction.TransactionService
import com.assignment.remit.transaction.model.TransactionStatus
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val transactionService: TransactionService
) {
    private fun get(id: Long): Account {
        return accountRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("계좌를 찾을 수 없습니다")
    }

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

    fun withdraw(request: WithdrawRequest, saveHistory: Boolean = false) {
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
}