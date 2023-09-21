package com.assignment.remit.user

import com.assignment.remit.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull

class UserServiceTest : FeatureSpec({
    val mockUserRepository = mockk<UserRepository>()
    val userService = UserService(mockUserRepository)

    val user = User("name", "01012341234")

    every { mockUserRepository.findByIdOrNull(0L)} returns null
    every { mockUserRepository.findByIdOrNull(1L)} returns user

    feature("findUserById: 사용자 조회") {
        scenario("주어진 id로 사용자를 찾을 수 없는 경우 EntityNotFoundException 예외 발생") {
            shouldThrowExactly<EntityNotFoundException> {
                userService.findUserById(0L)
            }
        }
        scenario("사용자 조회시 UserResponse 형태로 반환") {
            val userResponse = userService.findUserById(1L)
            userResponse.shouldBeInstanceOf<UserResponse>()
            userResponse.name shouldBe user.name
        }
    }

    feature("delete: 사용자 탈퇴") {
        scenario("사용자 탈퇴시 사용자의 deleted 값은 true로 변경") {
            user.deleted.shouldBeFalse()
            userService.delete(1L)
            user.deleted.shouldBeTrue()
        }
    }
})
