package com.assignment.remit.user

import com.assignment.remit.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun get(id: Long) =
        userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException()

    fun findUserById(id: Long) =
        UserResponse.of(get(id))

    fun create(createRequest: CreateUserRequest): UserResponse {
        val newUser = userRepository.save(User(createRequest.name, createRequest.mobile))
        return UserResponse.of(newUser)
    }

    fun delete(id: Long) {
        val foundUser = get(id)
        foundUser.delete()
    }
}