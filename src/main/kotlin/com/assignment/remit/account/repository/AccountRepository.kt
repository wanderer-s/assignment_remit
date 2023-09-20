package com.assignment.remit.account.repository

import com.assignment.remit.account.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository: JpaRepository<Account, Long>