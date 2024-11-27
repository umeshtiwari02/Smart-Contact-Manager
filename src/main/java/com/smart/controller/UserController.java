package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Messages;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

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
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		// Generate a unique value (e.g., time-stamp or UUID)
		String uniqueValue = String.valueOf(System.currentTimeMillis()).substring(6);

		// Create a unique image name
		String uniqueImageName = uniqueValue + "_" + file.getOriginalFilename();

		try {
			String name = principal.getName();

			User user = this.userRepository.getUserByUserName(name);

			// processing and uploading file
			if (file.isEmpty()) {
				System.out.println("File is empty!!");

				contact.setImage("contact.png");

			} else {

				// upload file into the folder and update the file name to the contact

				contact.setImage(uniqueImageName);

				File saveFile = new ClassPathResource("/static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + uniqueImageName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded!!");
			}

			user.getContacts().add(contact);
			contact.setUser(user);

			this.userRepository.save(user);

			System.out.println("Data : " + contact);

			System.out.println("Added to database");

			// message success
			session.setAttribute("message", new Messages("Your contact is added successfully!!", "success"));

			return "normal/add_contact_form";

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			// message error
			session.setAttribute("message", new Messages("Something went wrong. Please try again!!", "danger"));

			return "normal/add_contact_form";
		}
	}

	// show contact handler
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable int page, Model model, Principal principal) {

		String userName = principal.getName();

		User user = this.userRepository.getUserByUserName(userName);

		// Implementing Pagination here

		// currentPage - page
		// contact per-page - 5
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		model.addAttribute("title", "Show User Contacts");
		return "normal/show_contacts";
	}

	// showing particular contact details
	@GetMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable int cid, Model model, Principal principal) {

		System.out.println("CID  : " + cid);

		Optional<Contact> contactOptional = this.contactRepository.findById(cid);

		Contact contact = contactOptional.get();

		// Security for unauthorized user
		String userName = principal.getName(); // it get current user-id(email)

		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("title", contact.getName());
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}

	// delete contact handler
	@Transactional
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable int cid, Model model, Principal principal, HttpSession session) {

		// Security for unauthorized user
		Contact contact = this.contactRepository.findById(cid).get();
		String userName = principal.getName(); // it get current user-id(email)
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {

			Path imagePath = Paths.get("target/classes/static/img", contact.getImage());

			if (!contact.getImage().equals("contact.png")) {
				try {
					// deleting the profile image
					Files.delete(imagePath);

					System.out.println("Image Deleted Successfully!!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// deleting the contact
			this.contactRepository.deleteContactById(cid);
			// showing message
			session.setAttribute("message", new Messages("Contact deleted successfully!!", "success"));

		} else {
			session.setAttribute("message",
					new Messages("You don't have permission to delete this contact...", "danger"));
		}

		System.out.println("Deleted successfully");

		return "redirect:/user/show-contacts/0";
	}

	// update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable int cid, Model model) {

		model.addAttribute("title", "Update Contact");

		Contact contact = this.contactRepository.findById(cid).get();

		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler
	@PostMapping("/process-update")
	public String updateContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		// Generate a unique value (e.g., time-stamp or UUID)
		String uniqueValue = String.valueOf(System.currentTimeMillis()).substring(6);

		// Create a unique image name
		String uniqueImageName = uniqueValue + "_" + file.getOriginalFilename();

		try {
			// old contact details
			Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();

			// uploading new profile image
			if (!file.isEmpty()) {

				System.out.println("Image : " + oldContactDetail.getImage());
				// if the image is not the default image then delete it
				if (!oldContactDetail.getImage().equals("contact.png")) {

					// delete old photo
					File deleteFile = new ClassPathResource("static/img").getFile();

					File file2 = new File(deleteFile, oldContactDetail.getImage());

					file2.delete();
				}

				// update new photo
				contact.setImage(uniqueImageName);

				File saveFile = new ClassPathResource("/static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + uniqueImageName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded!!");

			} else {
				contact.setImage(oldContactDetail.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());

			contact.setUser(user);

			this.contactRepository.save(contact);

			session.setAttribute("message", new Messages("Your contact is updated successuflly!!", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/" + contact.getCid() + "/contact";

	}

	// your profile handler
	@GetMapping("/profile")
	public String profileHandler(Model model) {

		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	// profile form handler
	@PostMapping("/update-profile/{id}")
	public String updateProfileForm(@PathVariable int id, Model model) {

		model.addAttribute("title", "Update User");

		User user = this.userRepository.findById(id).get();

		model.addAttribute("user", user);

		return "normal/profile_update_form";
	}

	// update contact handler
	@PostMapping("/process-profile-update")
	public String updateUserProfile(@ModelAttribute User newUser, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session) {

		// Generate a unique value (e.g., time-stamp or UUID)
		String uniqueValue = String.valueOf(System.currentTimeMillis()).substring(7);

		// Create a unique image name
		String uniqueImageName = uniqueValue + "_u_" + file.getOriginalFilename();

		try {
			// old user details
			User oldUser = this.userRepository.findById(newUser.getId()).get();

			// uploading new profile image
			if (!file.isEmpty()) {

				// if the image is not the default image then delete it
				if (!oldUser.getImageUrl().equals("default.png")) {

					// delete old photo
					File deleteFile = new ClassPathResource("static/img").getFile();

					File file2 = new File(deleteFile, oldUser.getImageUrl());

					file2.delete();
				}

				// update new photo
				newUser.setImageUrl(uniqueImageName);

				File saveFile = new ClassPathResource("/static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + uniqueImageName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded!!");
			} else {
				newUser.setImageUrl(oldUser.getImageUrl());
			}

			// User newUserDetail =
			// this.userRepository.getUserByUserName(principal.getName());

			this.userRepository.save(newUser);

			session.setAttribute("message", new Messages("Your detail is updated successuflly!!", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("newUser", newUser);

		return "normal/profile";

	}

	// delete user handler
	@Transactional
	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable int id, Model model, Principal principal, HttpSession session) {

		// Get the current user
		User user = this.userRepository.getUserByUserName(principal.getName());

		// Delete the user's profile image
		Path userImagePath = Paths.get("target/classes/static/img", user.getImageUrl());
		if (!user.getImageUrl().equals("default.png")) {
			try {
				Files.delete(userImagePath);
				System.out.println("User image deleted successfully!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Fetch all contacts linked to the user
		List<Contact> contacts = user.getContacts(); // Assuming `contacts` is a mapped field in `User`

		for (Contact contact : contacts) {
			// Delete each contact's image
			Path contactImagePath = Paths.get("target/classes/static/img", contact.getImage());

			if (!contact.getImage().equals("contact.png")) {
				try {
					Files.delete(contactImagePath);
					System.out.println("Contact image deleted successfully for contact ID: " + contact.getCid());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// Delete the user and their contacts (assuming cascading is set up for
		// `contacts` in `User`)
		this.userRepository.delete(user);

		System.out.println("User and contacts deleted successfully");

		return "redirect:/signin";
	}

}
