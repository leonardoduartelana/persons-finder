package com.persons.finder.common.dto

import java.util.UUID

open class TransactionResponse<T>(
    val data: T,
) {
    val transactionId: String = UUID.randomUUID().toString()
}
