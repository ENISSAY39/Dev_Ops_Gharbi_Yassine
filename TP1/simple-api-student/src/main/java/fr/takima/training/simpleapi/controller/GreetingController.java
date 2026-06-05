package fr.takima.training.simpleapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/")
    public String greeting() {
        return "Hello World!";
    }

    @GetMapping("/instance")
    public String getInstance() {
        return System.getenv().getOrDefault("INSTANCE_NAME", "unknown");
    }
}