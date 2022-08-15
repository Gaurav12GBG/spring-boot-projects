package com.smart.smartcontactmanager.controllers;

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

@Controller
public class HomeController {

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
                    new Message("You have suceessfully registered in Smart Contact Manager", "alert-success"));

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
        }
        return "signup";
    }

}
