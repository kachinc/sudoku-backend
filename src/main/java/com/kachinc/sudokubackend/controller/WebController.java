package com.kachinc.sudokubackend.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
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

	@Autowired
	BuildProperties buildProperties;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("env",
				environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles()[0] : "dev");
		model.addAttribute("buildtime", buildProperties.getTime() == null ? RandomStringUtils.randomAlphabetic(5) : buildProperties.getTime().toEpochMilli());
		return "home";
	}

}
