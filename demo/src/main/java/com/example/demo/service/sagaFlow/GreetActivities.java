package com.example.demo.service.sagaFlow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GreetActivities {
    @ActivityMethod
    String greet(String name);
}

