package com.kachinc.sudokubackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Front end controller
 */
@Controller
public class WebController {
	
	@GetMapping("/")
    public String home(Model model) {
        return "home";
    }

}
