package com.assignment.remit.account

import com.assignment.remit.account.model.AccountStatus
import com.assignment.remit.global.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.math.BigDecimal

@Entity
class Account(
    balance: BigDecimal = BigDecimal.ZERO,
    depositLimit: BigDecimal,
    status: AccountStatus = AccountStatus.ACTIVE
): BaseEntity<Account>() {
    var balance = balance
        protected set

    var depositLimit = depositLimit
        protected set

    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    private fun availableCheck() {
        if (status != AccountStatus.ACTIVE) {
            throw AccountInactiveException()
        }
    }

    private fun depositAvailableCheck(amount: BigDecimal) {
        if ((balance + amount) > depositLimit) {
            throw DepositLimitExceededException()
        }
    }

    private fun withdrawAvailableCheck(amount: BigDecimal) {
        if ((balance - amount) < BigDecimal.ZERO) {
            throw BalanceNotEnoughException()
        }
    }

    fun deposit(amount: BigDecimal) {
        availableCheck()
        depositAvailableCheck(amount)
        balance += amount
    }

    fun toInActive() {
        if (balance > BigDecimal.ZERO) {
            throw RemainingBalanceExistsException()
        }

        status = AccountStatus.INACTIVE
    }
}