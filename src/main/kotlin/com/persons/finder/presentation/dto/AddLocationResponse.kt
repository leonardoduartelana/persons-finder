package com.persons.finder.presentation.dto

import com.persons.finder.common.dto.TransactionResponse
import com.persons.finder.data.Location

class AddLocationResponse(data: Location) : TransactionResponse<Location>(data)
