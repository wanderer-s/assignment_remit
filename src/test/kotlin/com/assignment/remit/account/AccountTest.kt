package com.assignment.remit.account

import com.assignment.remit.account.model.AccountStatus
import com.assignment.remit.global.exception.PermissionDeniedException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class AccountTest : FeatureSpec({
    feature("deposit: 입금") {
        scenario("계좌가 활성화 되어 있지 않은 경우 AccountInactiveException 예외 발생") {
            val account = createAccount(status = AccountStatus.INACTIVE)
            shouldThrowExactly<AccountInactiveException> {
                account.deposit(BigDecimal(10000))
            }
        }
        scenario("계좌의 보유 한도를 넘어선 금액을 입금하려고 시도할 경우 DepositLimitExceededException 예외 발생") {
            val account = createAccount(depositLimit = BigDecimal(10000))
            shouldThrowExactly<DepositLimitExceededException> {
                account.deposit(BigDecimal(50000))
            }
        }
        scenario("입금한 금액 만큼 잔고 증가") {
            val account = createAccount()
            val amount = BigDecimal(50000)
            account.deposit(amount)

            account.balance shouldBe amount
        }
    }
    feature("withdraw: 출금") {
        scenario("계좌가 활성화 되어 있지 않은 경우 AccountInactiveException 예외 발생") {
            val account = createAccount(status = AccountStatus.INACTIVE)
            shouldThrowExactly<AccountInactiveException> {
                account.withdraw(0L, BigDecimal(10000))
            }
        }
        scenario("출금을 시도하는 사용자와 계좌의 소유주가 다른 경우 PermissionDeniedException 예외 발생") {
            val account = createAccount()
            shouldThrowExactly<PermissionDeniedException> {
                account.withdraw(1L, BigDecimal(10000))
            }
        }
        scenario("계좌의 잔액을 넘어선 금액을 출금하려고 시도하면 BalanceNotEnoughException 예외 발생") {
            val account = createAccount(balance = BigDecimal(10000))
            shouldThrowExactly<BalanceNotEnoughException> {
                account.withdraw(0L, BigDecimal(50000))
            }
        }
        scenario("출금한 금액 만큼 잔고 감소") {
            val account = createAccount(balance = BigDecimal(10000))
            account.withdraw(0L, BigDecimal(5000))
            account.balance shouldBe BigDecimal(5000)
        }
    }
    feature("toInActive: 계좌 비활성화") {
        scenario("잔고가 0이 아닌 경우에 계좌 비활성화를 시도하면 RemainingBalanceExistsException 예외 발생") {
            val account = createAccount(balance = BigDecimal(10000))
            shouldThrowExactly<RemainingBalanceExistsException> {
                account.toInActive()
            }
        }
        scenario("잔고가 0인 경우 계좌 비활성화") {
            val noBalanceAccount = createAccount()
            noBalanceAccount.toInActive()

            noBalanceAccount.status shouldBe AccountStatus.INACTIVE
        }
    }
})
