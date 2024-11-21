package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("UserName : " + userName);

		User user = this.userRepository.getUserByUserName(userName);

		System.out.println("User Details :: " + user);

		model.addAttribute("user", user);

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
}
