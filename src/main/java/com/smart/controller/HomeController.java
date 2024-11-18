package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Messages;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
		model.addAttribute("user", new User());
		return "signup";
	}

	// Handler for register user

	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute User user, BindingResult result,
			@RequestParam(defaultValue = "false") boolean agreement, Model model, HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("You have not agreed the terms and conditions.");
				throw new Exception("You have not agreed the terms and conditions.");
			}

			if (result.hasErrors()) {
				System.out.println("ERROR " + result.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");

			System.out.println("Agreement : " + agreement);
			System.out.println("User : " + user);

			this.userRepository.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Messages("Successfully Registered !!! ", "alert-success"));
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Messages("Something went Wrong !! " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

}
