package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.smart.dao.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	
	// Home handler
	
	@GetMapping("/")
	public String home(Model model) {

		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	// About handler
	
	@GetMapping("/about")
	public String about(Model model) {

		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	// Sign Up Handler
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		
		return "signup";
	}

}
