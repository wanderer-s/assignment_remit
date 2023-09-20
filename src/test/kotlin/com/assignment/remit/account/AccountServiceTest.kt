package com.assignment.remit.account

import com.assignment.remit.account.dto.DepositRequest
import com.assignment.remit.account.dto.TransferRequest
import com.assignment.remit.account.dto.WithdrawRequest
import com.assignment.remit.account.model.AccountStatus
import com.assignment.remit.account.repository.AccountRepository
import com.assignment.remit.global.PermissionDeniedException
import com.assignment.remit.transaction.TransactionService
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

class AccountServiceTest : FeatureSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val mockAccountRepository = mockk<AccountRepository>(relaxed = true)
    val mockTransactionService = mockk<TransactionService>(relaxed = true)

    val accountService = AccountService(mockAccountRepository, mockTransactionService)

    val activeAccount = createAccount(depositLimit = BigDecimal(30000))
    val inactiveAccount = createAccount(status = AccountStatus.INACTIVE)
    val accountWithBalance = createAccount(balance = BigDecimal(50000))

    every { mockAccountRepository.findByIdOrNull(0L)} returns null
    every { mockAccountRepository.findByIdOrNull(1L)} returns activeAccount
    every { mockAccountRepository.findByIdOrNull(2L)} returns inactiveAccount
    every { mockAccountRepository.findByIdOrNull(3L)} returns accountWithBalance


    feature("deposit: 입금") {
        scenario("존재 하지 않는 계좌에 입금을 시도 하는 경우 EntityNotFoundException 예외 발생") {
            val depositRequest = DepositRequest(BigDecimal(10000))
            depositRequest.depositAccountId = 0L

            shouldThrowExactly<EntityNotFoundException> {
                accountService.deposit(depositRequest)
            }
        }
        scenario("비활성화 상태인 계좌에 입금을 시도 하는 경우 AccountInactiveException 예외 발생") {
            val depositRequest = DepositRequest(BigDecimal(10000))
            depositRequest.depositAccountId = 2L

            shouldThrowExactly<AccountInactiveException> {
                accountService.deposit(depositRequest)
            }
        }
        scenario("보유 한도를 초과하는 금액을 입금 시도 하는 경우 DepositLimitExceededException 예외 발생") {
            val depositRequest = DepositRequest(BigDecimal(1000000000))
            depositRequest.depositAccountId = 1L

            shouldThrowExactly<DepositLimitExceededException> {
                accountService.deposit(depositRequest)
            }
        }
        scenario("입금이 성공하면 입금한 금액만큼 잔고 증가") {
            val depositAmount = BigDecimal(10000)
            val depositRequest = DepositRequest(depositAmount)
            depositRequest.depositAccountId = 1L

            accountService.deposit(depositRequest)
            activeAccount.balance shouldBe depositAmount
        }
    }
    feature("withdraw: 출금") {
        scenario("존재 하지 않는 계좌에 출금을 시도 하는 경우 EntityNotFoundException 예외 발생") {
            val withdrawRequest = WithdrawRequest(BigDecimal(5000))
            withdrawRequest.withdrawAccountId = 0L
            withdrawRequest.userId = 10L

            shouldThrowExactly<EntityNotFoundException> {
                accountService.withdraw(withdrawRequest)
            }
        }
        scenario("비활성화 상태인 계좌에 출금을 시도 하는 경우 AccountInactiveException 예외 발생") {
            val withdrawRequest = WithdrawRequest(BigDecimal(5000))
            withdrawRequest.withdrawAccountId = 2L
            withdrawRequest.userId = 0L

            shouldThrowExactly<AccountInactiveException> {
                accountService.withdraw(withdrawRequest)
            }
        }
        scenario("출금을 시도하는 사람이 계좌의 소유주가 아닌 경우 PermissionDeniedException 예외 발생") {
            val withdrawRequest = WithdrawRequest(BigDecimal(5000))
            withdrawRequest.withdrawAccountId = 1L
            withdrawRequest.userId = 10L

            shouldThrowExactly<PermissionDeniedException> {
                accountService.withdraw(withdrawRequest)
            }
        }
        scenario("잔고를 초과하는 금액으로 출금 시도시 BalanceNotEnoughException 예외 발생") {
            val withdrawRequest = WithdrawRequest(BigDecimal(5000))
            withdrawRequest.withdrawAccountId = 1L
            withdrawRequest.userId = 0L

            shouldThrowExactly<BalanceNotEnoughException> {
                accountService.withdraw(withdrawRequest)
            }
        }
        scenario("출금이 성공하면 출금한 금액만큼 잔고 감소") {
            val depositRequest = DepositRequest(BigDecimal(10000))
            depositRequest.depositAccountId = 1L
            val withdrawRequest = WithdrawRequest(BigDecimal(5000))
            withdrawRequest.withdrawAccountId = 1L
            withdrawRequest.userId = 0L

            accountService.deposit(depositRequest)
            accountService.withdraw(withdrawRequest)

            activeAccount.balance shouldBe BigDecimal(5000)
        }
    }
    feature("transfer: 송금") {
        scenario("존재하지 않는 계좌로 송금 시도시 EntityNotFoundException 예외 발생") {
            val transferRequest = TransferRequest(1L, BigDecimal(10000))
            transferRequest.withdrawAccountId = 0L
            transferRequest.userId = 0L

            shouldThrowExactly<EntityNotFoundException> {
                accountService.transfer(transferRequest)
            }
        }
        scenario("송금을 시도하는 사람이 계죄의 소유주가 아닌 경우 PermissionDeniedException 예외 발생") {
            val transferRequest = TransferRequest(0L, BigDecimal(10000))
            transferRequest.withdrawAccountId = 1L
            transferRequest.userId = 10L

            shouldThrowExactly<PermissionDeniedException> {
                accountService.transfer(transferRequest)
            }
        }
        scenario("송금 시도 중 출금하는 지갑의 잔고가 부족한 경우 BalanceNotEnoughException 예외 발생") {
            val transferRequest = TransferRequest(0L, BigDecimal(10000))
            transferRequest.withdrawAccountId = 1L
            transferRequest.userId = 0L

            shouldThrowExactly<BalanceNotEnoughException> {
                accountService.transfer(transferRequest)
            }
        }
        scenario("송금대상의 보유 한도를 초과 하는 금액으로 송금을 시도하는 경우 DepositLimitExceededException 예외 발생") {
            val transferRequest = TransferRequest(1L, BigDecimal(40000))
            transferRequest.withdrawAccountId = 3L
            transferRequest.userId = 0L

            shouldThrowExactly<DepositLimitExceededException> {
                accountService.transfer(transferRequest)
            }
        }
        scenario("비활성화 계좌로 송금 시도 시 AccountInactiveException 예외 발생") {
            val transferRequest = TransferRequest(1L, BigDecimal(40000))
            transferRequest.withdrawAccountId = 2L
            transferRequest.userId = 0L

            shouldThrowExactly<AccountInactiveException> {
                accountService.transfer(transferRequest)
            }
        }
        scenario("송금이 성공하면 송금 금액 만큼 계좌의 잔고가 감소하고 증가한다") {
            val transferRequest = TransferRequest(1L, BigDecimal(10000))
            transferRequest.withdrawAccountId = 3L
            transferRequest.userId = 0L

            accountService.transfer(transferRequest)

            activeAccount.balance shouldBe BigDecimal(10000)
            accountWithBalance.balance shouldBe BigDecimal(40000)
        }
    }
})
