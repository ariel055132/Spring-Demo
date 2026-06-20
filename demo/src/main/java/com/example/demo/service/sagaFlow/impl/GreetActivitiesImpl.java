package com.example.demo.service.sagaFlow.impl;

import com.example.demo.service.sagaFlow.GreetActivities;

public class GreetActivitiesImpl implements GreetActivities {

    @Override
    public String greet(String name) {
        return "Hello " + name;
    }
}

