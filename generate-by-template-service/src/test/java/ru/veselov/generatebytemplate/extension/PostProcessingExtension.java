package ru.veselov.generatebytemplate.extension;

import lombok.Getter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        System.out.println("post processing extension");
        Field[] declaredFields = testInstance.getClass().getDeclaredFields();
        for (Field f : declaredFields) {
            if (f.isAnnotationPresent(Getter.class)) {
                System.out.println("annotation with field");
            }
        }
    }
}
