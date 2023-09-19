package com.assignment.remit.user

import com.assignment.remit.account.Account
import com.assignment.remit.account.model.AccountStatus
import com.assignment.remit.global.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Where
import java.math.BigDecimal

@Where(clause = "deleted = false")
@Entity
class User(
    name: String,
    mobile: String,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.PERSIST])
    val accounts: MutableSet<Account> = mutableSetOf()
): BaseEntity<User>() {
    var name = name
    protected set

    var mobile = mobile
    protected set

    @Column(nullable = false)
    var deleted = false
    protected set

    fun update(name: String, mobile: String) {
        this.name = name
        this.mobile = mobile
    }

    fun addAccount(depositLimit: BigDecimal) {
        accounts.add(Account(this, depositLimit))
    }

    fun delete() {
        accounts.filter { it.status != AccountStatus.INACTIVE }.forEach { it.toInActive() }
        deleted = true
    }
}