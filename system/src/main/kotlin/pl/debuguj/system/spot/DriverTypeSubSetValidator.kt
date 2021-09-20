package pl.debuguj.system.spot

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class DriverTypeSubSetValidator : ConstraintValidator<DriverTypeSubSet, DriverType> {

    private var subset: Array<DriverType> = emptyArray()

    override fun initialize(constraintAnnotation: DriverTypeSubSet?) {
        if (constraintAnnotation != null) {
            this.subset = constraintAnnotation.anyOf
        }
    }

    override fun isValid(value: DriverType?, context: ConstraintValidatorContext?): Boolean {
        return value == null || subset.toList().contains(value)
    }
}