package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.exceptions.UserAlreadyExistException;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return ("register");
    }

    @PostMapping("/register")
    public String submitRegisterForm(@Valid @ModelAttribute("user") UserDTO user, Errors errors, Model model) {

        if (errors.hasErrors()) {
            return "register";
        } else {
            try {
                this.userService.saveUser(user);
            } catch (UserAlreadyExistException userAlreadyExistException) {
                model.addAttribute("userAlreadyExistError", "Un compte est déjà associé à cet email");
                return "register";
            }
            return "redirect:login?registerSuccess";
        }
    }

}
