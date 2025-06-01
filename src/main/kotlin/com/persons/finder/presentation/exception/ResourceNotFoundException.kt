package com.persons.finder.presentation.exception

/**
 * Exception thrown when a requested resource is not found
 */
class ResourceNotFoundException(override val message: String) : RuntimeException(message)