package com.assignment.remit.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateUserRequest(
    @field:NotBlank
    val name: String,
    @field:Pattern(regexp = "^01[016789]\\d{3,4}\\d{4}$")
    val mobile: String,
)

data class UserResponse(
    val id: Long,
    val name: String,
    val mobile: String,
    val accountCount: Int
) {
    companion object {
        fun of(user: User) =
            UserResponse(
                user.id,
                user.name,
                user.mobile,
                user.accounts.size
            )
    }
}
