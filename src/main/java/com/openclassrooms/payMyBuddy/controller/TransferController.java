package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.UserAccountNotFoundException;
import com.openclassrooms.payMyBuddy.exceptions.UserBankAccountNotFoundException;
import com.openclassrooms.payMyBuddy.exceptions.UserNotFoundException;
import com.openclassrooms.payMyBuddy.service.BankAccountService;
import com.openclassrooms.payMyBuddy.service.TransferService;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
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
        log.info("GET /transfer called");
        model.addAttribute("debitTransfer", new TransferDTO());
        model.addAttribute("creditTransfer", new TransferDTO());
        log.info("Transfer page displayed");
        return ("transfer");
    }


    @PostMapping("/transfer/credit")
    public String creditAccount(@Valid @ModelAttribute("creditTransfer") TransferDTO transferDTO, Errors errors, Model model) throws UserNotFoundException, UserAccountNotFoundException, UserBankAccountNotFoundException {
        log.info("POST /transfer/credit called -> start of the process to make a credit transfer");
        UserDTO loggedUser = this.userService.getLoggedUserDTO();

        if (errors.hasErrors()) {
            log.info("Errors in form validation on field {}", errors.getFieldError().getField());
            model.addAttribute("debitTransfer", new TransferDTO());
            return ("transfer");
        }

        this.transferService.makeCreditTransfer(transferDTO, loggedUser.getMail());

        return ("redirect:../home?creditsuccess");
    }

    @PostMapping("/transfer/debit")
    public String debitAccount(@Valid @ModelAttribute("debitTransfer") TransferDTO transferDTO, Errors errors, Model model) throws UserNotFoundException, UserAccountNotFoundException, UserBankAccountNotFoundException {
        log.info("POST /transfer/debit called -> start of the process to make a debit transfer");
        UserDTO loggedUser = this.userService.getLoggedUserDTO();

        if (errors.hasErrors()) {
            log.info("Errors in form validation on field {}", errors.getFieldError().getField());
            model.addAttribute("creditTransfer", new TransferDTO());
            return ("transfer");
        }

        try {
            this.transferService.makeDebitTransfer(transferDTO, loggedUser.getMail());
        } catch (InsufficientBalanceException e) {
            log.info("insufficient balance to make this transfer");
            return ("redirect:../home?debiterror");
        }
        return ("redirect:../home?debitsuccess");
    }

}
