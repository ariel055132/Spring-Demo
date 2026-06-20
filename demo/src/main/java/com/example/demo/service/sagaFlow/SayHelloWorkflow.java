package com.example.demo.service.sagaFlow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SayHelloWorkflow {
    @WorkflowMethod
    String sayHello(String name);
} 
