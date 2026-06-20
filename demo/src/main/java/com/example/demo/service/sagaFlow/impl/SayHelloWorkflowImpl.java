package com.example.demo.service.sagaFlow.impl;

import java.time.Duration;

import com.example.demo.service.sagaFlow.GreetActivities;
import com.example.demo.service.sagaFlow.SayHelloWorkflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

public class SayHelloWorkflowImpl implements SayHelloWorkflow {

    private final GreetActivities activities = Workflow.newActivityStub(
        GreetActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .build()
    );

    @Override
    public String sayHello(String name) {
        return activities.greet(name);
    }
    
}
