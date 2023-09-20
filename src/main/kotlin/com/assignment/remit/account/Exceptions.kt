package com.assignment.remit.account

import com.assignment.remit.global.CustomException


sealed class AccountException(message: String): CustomException(message)
class AccountInactiveException(message: String = "계좌 비활성화"): AccountException(message)
class DepositLimitExceededException(message: String = "계좌 보유 한도 초과"): AccountException(message)
class BalanceNotEnoughException(message: String = "잔고 부족"): AccountException(message)
class RemainingBalanceExistsException(message: String = "잔고가 남아 있습니다"): AccountException(message)