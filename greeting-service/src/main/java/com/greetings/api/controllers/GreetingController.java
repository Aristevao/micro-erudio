package com.greetings.api.controllers;

import com.greetings.api.configuration.GreetingConfiguration;
import com.greetings.api.model.Greeting;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@RestController
public class GreetingController {

    private final GreetingConfiguration configuration;

    private static final String template = "%s, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greetings")
    public Greeting greeting(
            @RequestParam(value = "name",
                    defaultValue = "") String name) {
        if (name.isEmpty()) name = configuration.getDefaultValue();

        return new Greeting(
                counter.incrementAndGet(),
                String.format(template, configuration.getGreeting(), name)
        );
    }
}
