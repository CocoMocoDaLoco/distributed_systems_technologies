package dst.ass1.jpa.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CpuCountValidator implements ConstraintValidator<CPUs, Integer> {

    private int max;
    private int min;

    @Override
    public void initialize(CPUs annotation) {
        min = annotation.min();
        max = annotation.max();
    }

    @Override
    public boolean isValid(Integer object, ConstraintValidatorContext constraintContext) {
        System.out.printf("%s: isValid%n", CpuCountValidator.class.getName());
        int i = object.intValue();
        return (i <= max && i >= min);
    }

}
