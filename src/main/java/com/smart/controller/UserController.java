package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	// methods for adding common data to response

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("UserName : " + userName);

		// get the user using user-name(email)
		User user = this.userRepository.getUserByUserName(userName);

		System.out.println("User Details :: " + user);

		model.addAttribute("user", user);
	}

	// user dash-board

	@GetMapping("/index")
	public String dashboard(Model model) {

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	// processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, Principal principal) {

		String name = principal.getName();

		User user = this.userRepository.getUserByUserName(name);

		contact.setUser(user);
		user.getContacts().add(contact);

		this.userRepository.save(user);

		System.out.println("Data : " + contact);

		System.out.println("Added to database");
		return "normal/add_contact_form";
	}

}
