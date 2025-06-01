package com.persons.finder.presentation.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [LatitudeValidator::class])
annotation class ValidLatitude(
    val message: String = "Invalid latitude. Must be between -90 and 90 degrees",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = []
)

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [LongitudeValidator::class])
annotation class ValidLongitude(
    val message: String = "Invalid longitude. Must be between -180 and 180 degrees",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = []
)

class LatitudeValidator : ConstraintValidator<ValidLatitude, Double> {
    override fun isValid(value: Double?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        return value >= -90.0 && value <= 90.0
    }
}

class LongitudeValidator : ConstraintValidator<ValidLongitude, Double> {
    override fun isValid(value: Double?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        return value >= -180.0 && value <= 180.0
    }
} 