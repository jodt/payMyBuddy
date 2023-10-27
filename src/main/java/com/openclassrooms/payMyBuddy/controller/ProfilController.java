package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.service.BankAccountService;
import com.openclassrooms.payMyBuddy.service.TransferService;
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

    private final TransferService transferService;


    public ProfilController(UserService userService, BankAccountService bankAccountService, TransferService transferService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.transferService = transferService;
    }


    @GetMapping("/profil")
    public String getProfil(Model model) {
        UserDTO loggedUser = this.userService.getLoggedUserDTO();
        BankAccountDTO userBankAccountDTO = this.bankAccountService.findBankAccountDTOByUserMail(loggedUser.getMail());

        model.addAttribute("userBankAccount", userBankAccountDTO);
        model.addAttribute("creditTransfer", new TransferDTO());
        model.addAttribute("debitTransfer", new TransferDTO());
        model.addAttribute("loggedUser", loggedUser);
        return ("profil");
    }

    @PostMapping("/profil")
    public String saveUserBankAccount(@Valid @ModelAttribute("userBankAccount") BankAccountDTO bankAccountDTO, Errors errors) {
        UserDTO loggedUser = this.userService.getLoggedUserDTO();
        if (errors.hasErrors()) {
            System.out.println(errors);
            return ("redirect:profil?ibanError");
        }
        this.bankAccountService.save(bankAccountDTO, loggedUser.getMail());

        return ("redirect:profil");
    }

    @PostMapping("/profil/credit")
    public String creditAccount(@Valid @ModelAttribute("creditTransfer") TransferDTO transferDTO, Errors errors, Model model) {
        UserDTO loggedUser = this.userService.getLoggedUserDTO();
        BankAccountDTO userBankAccountDTO = this.bankAccountService.findBankAccountDTOByUserMail(loggedUser.getMail());

        if (errors.hasErrors()) {
            System.out.println(errors);
            model.addAttribute("userBankAccount", userBankAccountDTO);
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("debitTransfer", new TransferDTO());
            return ("profil");
        }

        this.transferService.makeCreditTransfer(transferDTO, loggedUser.getMail());

        return ("redirect:../home?creditsuccess");
    }

    @PostMapping("/profil/debit")
    public String debitAccount(@Valid @ModelAttribute("debitTransfer") TransferDTO transferDTO, Errors errors, Model model) {
        UserDTO loggedUser = this.userService.getLoggedUserDTO();
        BankAccountDTO userBankAccountDTO = this.bankAccountService.findBankAccountDTOByUserMail(loggedUser.getMail());

        if (errors.hasErrors()) {
            if (errors.hasErrors()) {
                System.out.println(errors);
                model.addAttribute("userBankAccount", userBankAccountDTO);
                model.addAttribute("loggedUser", loggedUser);
                model.addAttribute("creditTransfer", new TransferDTO());
                return ("profil");
            }
        }
        try {
            this.transferService.makeDebitTransfer(transferDTO, loggedUser.getMail());
        } catch (InsufficientBalanceException e) {
            return ("redirect:../home?debiterror");
        }
        return ("redirect:../home?debitsuccess");
    }

}
