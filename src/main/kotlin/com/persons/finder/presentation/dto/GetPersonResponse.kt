package com.persons.finder.presentation.dto

import com.persons.finder.common.dto.TransactionResponse
import com.persons.finder.data.Person

class GetPersonResponse(data: List<Person>) : TransactionResponse<List<Person>>(data)
