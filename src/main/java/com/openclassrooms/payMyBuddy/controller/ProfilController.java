package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BankAccountMapper;
import com.openclassrooms.payMyBuddy.service.BankAccountService;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfilController {

    private final UserService userService;

    private final BankAccountService bankAccountService;

    private final BankAccountMapper bankAccountMapper;

    public ProfilController(UserService userService, BankAccountService bankAccountService, BankAccountMapper bankAccountMapper) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.bankAccountMapper = bankAccountMapper;
    }


    @GetMapping("/profil")
    public String getProfil(Model model) {
        UserDTO loggedUser = this.userService.getLoggedUserDTO();
        BankAccountDTO userBankAccountDTO = this.bankAccountService.findBankAccountDTOByUserMail(loggedUser.getMail());

        model.addAttribute("userBankAccount", userBankAccountDTO);
        model.addAttribute("loggedUser", loggedUser);
        return ("profil");
    }

    @PostMapping("/profil")
    public String saveUserBankAccount(@Valid @ModelAttribute("userBankAccount") BankAccountDTO bankAccountDTO, Errors errors, Model model) {
        UserDTO loggedUser = this.userService.getLoggedUserDTO();
        if (errors.hasErrors()) {
            return ("redirect:profil?git add ibanError");
        }
        this.bankAccountService.save(bankAccountDTO, loggedUser.getMail());

        return ("redirect:profil");
    }

}
