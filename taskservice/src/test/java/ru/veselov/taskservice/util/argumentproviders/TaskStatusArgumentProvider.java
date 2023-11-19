package ru.veselov.taskservice.util.argumentproviders;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.veselov.taskservice.entity.TaskStatus;

import java.util.stream.Stream;

public class TaskStatusArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(Arguments.of(TaskStatus.PERFORMED), Arguments.of(TaskStatus.FAILED));
    }

}
