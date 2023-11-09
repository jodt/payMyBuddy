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
public class TransferController {

    private final TransferService transferService;

    private final UserService userService;


    public TransferController(TransferService transferService, UserService userService) {
        this.transferService = transferService;
        this.userService = userService;
    }

    @GetMapping("/transfer")
    public String showTransferPage(Model model) {
        model.addAttribute("debitTransfer", new TransferDTO());
        model.addAttribute("creditTransfer", new TransferDTO());
        return ("transfer");
    }


    @PostMapping("/transfer/credit")
    public String creditAccount(@Valid @ModelAttribute("creditTransfer") TransferDTO transferDTO, Errors errors, Model model) {
        UserDTO loggedUser = this.userService.getLoggedUserDTO();

        if (errors.hasErrors()) {
            model.addAttribute("debitTransfer", new TransferDTO());
            return ("transfer");
        }

        this.transferService.makeCreditTransfer(transferDTO, loggedUser.getMail());

        return ("redirect:../home?creditsuccess");
    }

    @PostMapping("/transfer/debit")
    public String debitAccount(@Valid @ModelAttribute("debitTransfer") TransferDTO transferDTO, Errors errors, Model model) {
        UserDTO loggedUser = this.userService.getLoggedUserDTO();

        if (errors.hasErrors()) {
            model.addAttribute("creditTransfer", new TransferDTO());
            return ("transfer");
        }

        try {
            this.transferService.makeDebitTransfer(transferDTO, loggedUser.getMail());
        } catch (InsufficientBalanceException e) {
            return ("redirect:../home?debiterror");
        }
        return ("redirect:../home?debitsuccess");
    }

}
