package com.assignment.remit.account

class AccountInactiveException(message: String = "계좌 비활성화"): IllegalArgumentException(message)
class DepositLimitExceededException(message: String = "계좌 보유 한도 초과"): IllegalArgumentException(message)
class BalanceNotEnoughException(message: String = "잔고 부족"): IllegalArgumentException(message)
class RemainingBalanceExistsException(message: String = "잔고가 남아 있습니다"): IllegalArgumentException(message)