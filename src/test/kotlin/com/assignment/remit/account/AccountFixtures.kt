package com.assignment.remit.account

import com.assignment.remit.account.model.AccountStatus
import com.assignment.remit.user.createUser
import java.math.BigDecimal

fun createAccount(depositLimit: BigDecimal = BigDecimal(1000000), balance: BigDecimal = BigDecimal.ZERO, status: AccountStatus = AccountStatus.ACTIVE) =
    Account(createUser(), depositLimit, balance, status)