package com.smart.smartcontactmanager.controllers;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helpers.Message;
import com.smart.smartcontactmanager.services.EmailService;

@Controller
public class HomeController {

    // for generating random number OR OTP
    Random random = new Random();

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // handler for home
    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("title", "Home - Smart Contact Manager");
        model.addAttribute("attach1", true);
        return "home";
    }

    // handler for about
    @GetMapping("/about")
    public String about(Model model) {

        model.addAttribute("title", "About - Smart Contact Manager");
        model.addAttribute("attach2", true);

        return "about";
    }

    // handler for login
    @GetMapping("/signin")
    public String login(Model model) {

        model.addAttribute("title", "Login - Smart Contact Manager");
        model.addAttribute("attach3", true);

        return "login";
    }

    // handler for signup
    @GetMapping("/signup")
    public String signup(Model model) {

        model.addAttribute("title", "Signup - Smart Contact Manager");
        model.addAttribute("attach4", true);

        model.addAttribute("user", new User());
        return "signup";
    }

    // handler for registring user
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
            HttpSession session) {

        model.addAttribute("attach4", true);
        try {
            // if (!agreement) {
            // // System.out.println("You have not agreed the terms and conditions");
            // throw new Exception("You have not agreed the terms and conditions");
            // }

            if (result.hasErrors()) {
                System.out.println("Error Occured " + result.toString());
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            System.out.println("Agreement= " + agreement);
            System.out.println("User= " + user);

            this.userRepository.save(user);

            model.addAttribute("user", new User());
            session.setAttribute("message",
                    new Message("You have suceessfully registered in Smart Contact Manager", "alert-info"));

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
        }
        return "signup";
    }

    // OpenForgot password
    @GetMapping("/forgotPassword")
    public String openForgotPassword(Model model) {

        model.addAttribute("title", "Forgot-Password?");

        return "forgotPassword";
    }

    // Send OTP
    @PostMapping("/send-OTP")
    public String sendOTP(Model model, @RequestParam("email") String email, HttpSession session) {

        model.addAttribute("title", "do-Forgot-Password?");

        // Generating OTP
        int otp = 100000 + random.nextInt(900000);

        // Write a code for sending OTP to the email
        String subject = "OTP from Smart Contact Manager";
        String to = email;
        String message = ""
                + "<div style='border:1px solid purple;padding:20px;'>"
                + "<p>Dear " + email
                + "</p>"
                + "<p>Your OTP for forgot password of Smart Contact Manager Account is <h4>" + otp + "</h4>"
                + "</p>"
                + "</div>";

        boolean response = this.emailService.sendEmail(subject, message, to);

        if (response) {

            // store the values in session
            session.setAttribute("myOtp", otp);
            session.setAttribute("myEmail", email);
            session.setAttribute("message", new Message("Your OTP has been send successfully!", "alert-info"));
            return "verify_OTP";

        } else {
            session.setAttribute("message", new Message("Check your email id!!", "alert-danger"));
            return "forgotPassword";
        }

    }

    // Verify OTP
    @PostMapping("/verify-OTP")
    public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {

        int myOtp = (int) session.getAttribute("myOtp");
        String email = (String) session.getAttribute("myEmail");

        // Getting user details
        User user = this.userRepository.getUserByUserName(email);

        if (myOtp == otp) {

            if (user == null) {
                session.setAttribute("message",
                        new Message("User does not exists! Check your email id !", "alert-danger"));
                return "redirect:/forgotPassword";
            } else {
                return "change_password";
            }

        } else {
            // error
            session.setAttribute("message", new Message("You have entered wrong OTP!", "alert-danger"));
            return "verify_OTP";
        }
    }

    // Change Password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword, HttpSession session) {

        String email = (String) session.getAttribute("myEmail");

        // get user
        User user = this.userRepository.getUserByUserName(email);

        // Check new password and confirm new password
        if (newPassword.equals(confirmNewPassword)) {

            // change the password
            user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Password Changed Successfully!!!", "alert-success"));
            return "redirect:/signin";

        } else {
            session.setAttribute("message", new Message("Password does not match! Try again...", "alert-danger"));
            return "change_password";
        }

    }

}
