package com.smart.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {

	// generating 4 digits OTP
	Random random = new Random(10000);

	// email-id form handler for forgot password
	@GetMapping("/forgot")
	public String openEmailForm() {

		return "forgot_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam String email) {

		int otp = random.nextInt(99999);
		
		System.out.println("Email : " + email);
		System.out.println("OTP : " + otp);
		
		// code for sending OTP to your email

		return "verify_otp";
	}

}
