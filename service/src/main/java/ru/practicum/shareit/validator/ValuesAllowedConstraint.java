package ru.practicum.shareit.validator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ValuesAllowedConstraint {

    String message() default "{value.hasWrong}";

    Class<?>[] groups() default {};

    String propName();

    String[] values();
}
