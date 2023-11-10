package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.service.BankAccountService;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class ProfilController {

    private final UserService userService;

    private final BankAccountService bankAccountService;


    public ProfilController(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }


    @GetMapping("/profil")
    public String showProfilePage(Model model) throws ResourceNotFoundException {
        log.info("GET /profil called");
        UserDTO loggedUser = this.userService.getLoggedUserDTO();
        BankAccountDTO userBankAccountDTO = this.bankAccountService.findBankAccountDTOByUserMail(loggedUser.getMail());
        model.addAttribute("userBankAccount", userBankAccountDTO);
        model.addAttribute("loggedUser", loggedUser);
        log.info("Profile page displayed");
        return ("profil");
    }

    @PostMapping("/profil")
    public String saveUserBankAccount(@Valid @ModelAttribute("userBankAccount") BankAccountDTO bankAccountDTO, Errors errors) throws ResourceNotFoundException {
        log.info("POST /profil called -> start of the process to create user bank account");
        UserDTO loggedUser = this.userService.getLoggedUserDTO();
        if (errors.hasErrors()) {
            log.info("Errors in form validation on field {}", errors.getFieldError().getField());
            return ("redirect:profil?ibanError");
        }
        this.bankAccountService.save(bankAccountDTO, loggedUser.getMail());

        log.info("End of the process to create user bank account");
        return ("redirect:profil");
    }

}
