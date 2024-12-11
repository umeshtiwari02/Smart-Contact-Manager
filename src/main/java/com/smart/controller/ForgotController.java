package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	Random random = new Random(10000);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	// email-id form handler for forgot password
	@GetMapping("/forgot")
	public String openEmailForm() {

		return "forgot_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam String email, HttpSession session) {

		// generating 5 digits OTP
		int otp = random.nextInt(99999);

		System.out.println("Email : " + email);
		System.out.println("OTP : " + otp);

		// code for sending OTP to your email

		String subject = "OTP from Smart Contact Manager";
		String message = "" + "<div>" + "<h2>" + "Your OTP is " + otp + "</h2>" + "</div>";
		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {

			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		} else {
			session.setAttribute("message", "Please check your email id!!");

			return "forgot_email_form";
		}

	}

	// verifying OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam int otp, HttpSession session) {

		int myOtp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");

		if (myOtp == otp) {

			// password change form
			User user = this.userRepository.getUserByUserName(email);

			if (user == null) {
				// send error message
				session.setAttribute("message", "User does not exist with this email!!");

				return "forgot_email_form";
			} else {
				// send to change password form
				return "password_change_form";
			}

		} else {

			session.setAttribute("message", "You have entered wrong OTP!!");
			return "verify_otp";
		}

	}

}
