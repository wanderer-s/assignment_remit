package com.assignment.remit.transaction.repository.impl

import com.assignment.remit.RepositoryTest
import com.assignment.remit.transaction.Filter
import com.assignment.remit.transaction.Pagination
import com.assignment.remit.transaction.createTransaction
import com.assignment.remit.transaction.model.TransactionFilterStatus
import com.assignment.remit.transaction.model.TransactionType
import com.assignment.remit.transaction.repository.TransactionRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class TransactionCustomRepositoryImplTest(
    private val transactionRepository: TransactionRepository
) : FeatureSpec({
    val result = listOf(
        createTransaction(1, 2),
        createTransaction(2, 1),
        createTransaction(1, null, TransactionType.DEPOSIT),
        createTransaction(null, 2, TransactionType.WITHDRAW),
        createTransaction(3, 2)
    )
    transactionRepository.saveAll(result)

    feature("findAllByFilter: filter 조건으로 transaction 조회") {
        scenario("income으로 조회 시 결과가 2개") {
            val filter = Filter(accountId = 1L, status = TransactionFilterStatus.INCOME)

            val result = transactionRepository.findAllByFilter(filter, Pagination(10, null))
            result.size shouldBe 2
        }

        scenario("결과가 총 2개인 상태에서 pagination limit을 1로 조회시 결과는 1개") {
            val filter = Filter(accountId = 1L, status = TransactionFilterStatus.INCOME)

            val result = transactionRepository.findAllByFilter(filter, Pagination(1, null))
            result.size shouldBe 1
        }

        scenario("spend로 조회 시 결과값 3개") {
            val filter = Filter(accountId = 2L, status = TransactionFilterStatus.SPEND)

            val result = transactionRepository.findAllByFilter(filter, Pagination(10, null))
            result.size shouldBe 3
        }

        scenario("cursor 기준으로 pagination 적용해서 조회시도 시 결과값 1개") {
            val filter = Filter(accountId = 2L, status = TransactionFilterStatus.SPEND)

            val result = transactionRepository.findAllByFilter(filter, Pagination(10, 4))
            result.size shouldBe 1
        }
    }
})
