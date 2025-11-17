package com.spring.ollama.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class uiController {

    @GetMapping("/")
    public String home() {
        return "chat";
    }
}
