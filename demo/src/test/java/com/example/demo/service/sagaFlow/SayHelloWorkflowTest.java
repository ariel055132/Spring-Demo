package com.example.demo.service.sagaFlow;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.service.sagaFlow.impl.GreetActivitiesImpl;
import com.example.demo.service.sagaFlow.impl.SayHelloWorkflowImpl;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SayHelloWorkflowTest {

    private static final String TASK_QUEUE = "my-task-queue";

    private TestWorkflowEnvironment testEnvironment;

    @BeforeEach
    void setUp() {
        testEnvironment = TestWorkflowEnvironment.newInstance();
        Worker worker = testEnvironment.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(SayHelloWorkflowImpl.class);
        worker.registerActivitiesImplementations(new GreetActivitiesImpl());
        testEnvironment.start();
    }

    @AfterEach
    void tearDown() {
        testEnvironment.close();
    }

    @Test
    void sayHelloReturnsGreetingFromActivity() {
        SayHelloWorkflow workflow = testEnvironment.getWorkflowClient().newWorkflowStub(
            SayHelloWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .build()
        );

        String greeting = workflow.sayHello("Codex");

        assertThat(greeting).isEqualTo("Hello Codex");
    }
}
