package ru.eugene.planningpoker.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForwardingController {

    @GetMapping(value = {"/room/**", "/home", "/"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
