package com.assignment.remit.account

import com.assignment.remit.account.model.AccountStatus
import com.assignment.remit.global.BaseEntity
import com.assignment.remit.global.exception.PermissionDeniedException
import com.assignment.remit.user.User
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Account(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    val user: User,
    depositLimit: BigDecimal,
    balance: BigDecimal = BigDecimal.ZERO,
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

    private fun authorityCheck(userId: Long) {
        if (userId != user.id) {
            throw PermissionDeniedException()
        }
    }

    fun deposit(amount: BigDecimal) {
        availableCheck()
        depositAvailableCheck(amount)
        balance += amount
    }

    fun withdraw(userId: Long, amount: BigDecimal) {
        authorityCheck(userId)
        availableCheck()
        withdrawAvailableCheck(amount)
        balance -= amount
    }

    fun toInActive() {
        if (balance > BigDecimal.ZERO) {
            throw RemainingBalanceExistsException()
        }

        status = AccountStatus.INACTIVE
    }
}