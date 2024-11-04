package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.smart.dao.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	
}
