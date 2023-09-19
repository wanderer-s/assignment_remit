package com.assignment.remit.user

import com.assignment.remit.account.RemainingBalanceExistsException
import com.assignment.remit.account.model.AccountStatus
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class UserTest : FeatureSpec({
    val user = createUser()
    feature("update: 사용자 정보 수정") {
        val updateName = "update name"
        val updateMobile = "01056784321"
        user.update(updateName, updateMobile)

        user.name shouldBe updateName
        user.mobile shouldBe updateMobile
    }

    feature("addAccount: 계좌 추가") {
        user.accounts.size.shouldBeZero()
        user.addAccount(BigDecimal(50000))
        user.accounts.size shouldBe 1
    }

    feature("delete: 사용자 삭제") {
        scenario("사용자 소유의 계좌에 잔고가 0 이상인 경우 RemainingBalanceExistsException 예외 발생") {
            user.addAccount(BigDecimal(50000))
            user.accounts.forEach { it.deposit(BigDecimal(10000)) }

            shouldThrowExactly<RemainingBalanceExistsException> {
                user.delete()
            }
        }
        scenario("사용자 소유의 계좌에 잔고가 없는 경우 사용자 삭제처리 되며 소유한 계좌는 비활성화 처리") {
            user.accounts.forEach { it.withdraw(user.id, BigDecimal(10000)) }

            user.delete()

            user.deleted.shouldBeTrue()
            val account = user.accounts.first()
            account.status shouldBe AccountStatus.INACTIVE
        }
    }
})
