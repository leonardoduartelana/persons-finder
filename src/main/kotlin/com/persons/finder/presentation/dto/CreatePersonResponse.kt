package com.persons.finder.presentation.dto

import com.persons.finder.common.dto.TransactionResponse
import com.persons.finder.data.Person

class CreatePersonResponse(data: Person) : TransactionResponse<Person>(data)
