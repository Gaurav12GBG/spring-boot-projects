package com.smart.smartcontactmanager.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.smartcontactmanager.dao.ContactRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helpers.Message;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // Method for adding common data to the response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);
        model.addAttribute("user", user);

    }

    @RequestMapping(value = "/index")
    public String dashboard(Model model, Principal principal) {

        model.addAttribute("title", "User_Dashboard - SCM");
        model.addAttribute("attach1", true);
        return "normal/user_dashboard";
    }

    // Open add-contact handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {

        model.addAttribute("title", "Add Contact - SCM");
        model.addAttribute("contact", new Contact());
        model.addAttribute("attach2", true);

        return "normal/add_contact_form";
    }

    // Processing add contcat form
    @PostMapping("/processContact")
    public String processContact(Model model, @Valid @ModelAttribute("contact") Contact contact, BindingResult result,
            @RequestParam("image") MultipartFile file, Principal principal, HttpSession session) {

        try {
            model.addAttribute("attach2", true);

            String UserName = principal.getName();
            User user = userRepository.getUserByUserName(UserName);

            contact.setUser(user);
            user.getContacts().add(contact);

            // Processing and uploading file
            if (file.isEmpty()) {
                // if the file is empty then try your message
                System.out.println("Image is Empty!");
                contact.setImage("add-user.png");
            } else {
                // upload the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File savefile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");
            }

            this.userRepository.save(user);

            // message success
            session.setAttribute("message", new Message("Contact Added Successfully!!!", "alert-success"));

        } catch (Exception e) {
            e.printStackTrace();
            // message error
            session.setAttribute("message", new Message("Something Went Wrong ! try again...", "alert-danger"));

        }

        return "normal/add_contact_form";
    }

    // Show contacts handler
    // per pages = 5[n]
    // current page = 0[page]
    @GetMapping("/showContacts/{page}")
    public String showContats(@PathVariable("page") int page, Model model, Principal principal) {

        model.addAttribute("title", "Show Contacts- SCM");
        model.addAttribute("attach3", true);

        // We have to send the list of contact from here
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        int userId = user.getId();

        // currentPage - page
        // Contacts per page - 5
        Pageable pageable = PageRequest.of(page, 3);
        Page<Contact> contacts = this.contactRepository.findContactByUser(userId, pageable);

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show_contacts";
    }

    // Showing specific contact details
    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") int cId, Model model, Principal principal) {

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if (user.getId() == contact.getUser().getId()) {

            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
            model.addAttribute("attach3", true);

        }

        return "normal/contact_detail";
    }

    // Deletion of the specific contact
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") int cId, Model model, Principal principal, HttpSession session) {

        Optional<Contact> cOptional = this.contactRepository.findById(cId);
        Contact contact = cOptional.get();

        // Check who is delete this contact
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if (user.getId() == contact.getUser().getId()) {

            // Here directly contact not deleted in such cases so we need to remove
            // cascade.ALL OR unlinked the user, So the better way is that, unlinked the
            // user....that set
            contact.setUser(null);
            this.contactRepository.delete(contact);

        }
        model.addAttribute("attach3", true);

        session.setAttribute("message", new Message("Contact Deleted Successfully!!!", "alert-danger"));
        return "redirect:/user/showContacts/0";
    }

    // Updation of the specific contact
    @PostMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") int cId, Model model, Principal principal) {

        Optional<Contact> cOptional = this.contactRepository.findById(cId);
        Contact contact = cOptional.get();

        model.addAttribute("title", "Update Contact - SCM");
        model.addAttribute("contact", contact);
        model.addAttribute("attach3", true);


        return "normal/update_form";
    }

    @PostMapping("/process-update")
    public String updateContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
            @RequestParam("image") MultipartFile file, Model model, HttpSession session, Principal principal) {

        try {
            model.addAttribute("attach3", true);

            // fetch old contact details
            Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();

            if (!file.isEmpty()) {

                // rewrite file
                // 1. delete the old photo
                File deletefile = new ClassPathResource("static/images").getFile();
                File file1 = new File(deletefile, oldContactDetail.getImage());
                file1.delete();

                // 2. update new photo
                File savefile = new ClassPathResource("static/images").getFile();
                Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());
            } else {
                contact.setImage(oldContactDetail.getImage());
            }

            String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);

            contact.setUser(user);
            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Contact Updated Successfully!!!", "alert-success"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/user/" + contact.getcId() + "/contact";
    }

    // Profile Handler
    @GetMapping("/profile")
    public String profile(Model model) {

        model.addAttribute("title", "User Profile - SCM");
        model.addAttribute("attach4", true);

        return "normal/my_profile";
    }

}
