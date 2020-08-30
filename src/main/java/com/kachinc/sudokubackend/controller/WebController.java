package com.kachinc.sudokubackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Front end controller
 */
@Controller
public class WebController {
	
	@Autowired
	private Environment environment;
	
	@GetMapping("/")
    public String home(Model model) {
		model.addAttribute("env", environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles()[0] : "dev");
        return "home";
    }

}
