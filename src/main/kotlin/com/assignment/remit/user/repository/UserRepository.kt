package com.assignment.remit.user.repository

import com.assignment.remit.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long>