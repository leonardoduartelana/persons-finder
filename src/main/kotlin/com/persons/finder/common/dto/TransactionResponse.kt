package com.persons.finder.common.dto

import java.util.UUID

data class TransactionResponse<T>(
    val transactionId: String = UUID.randomUUID().toString(),
    val data: T,
)
