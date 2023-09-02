package ru.veselov.transducersmanagingservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marking object that receive Request parameters page= sort= order= and convert to SortParameters in
 * SortParameterRequestParamResolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SortingParam {
}
