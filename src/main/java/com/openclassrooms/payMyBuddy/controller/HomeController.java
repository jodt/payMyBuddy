package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.AccountMapper;
import com.openclassrooms.payMyBuddy.service.AccountService;
import com.openclassrooms.payMyBuddy.service.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Controller
public class HomeController {

    private final AccountService accountService;

    private final BankAccountService bankAccountService;

    public HomeController(AccountService accountService, AccountMapper accountMapper, BankAccountService bankAccountService) {
        this.accountService = accountService;
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/home")
    public String showHomePage(Principal principal, Model model) {
        log.info("GET /Home called");
        AccountDTO userAccount = this.accountService.findAccountDtoByUserMail(principal.getName());
        BankAccountDTO userBankAccountDto = this.bankAccountService.findBankAccountDTOByUserMail(principal.getName());
        model.addAttribute("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("bankAccount", userBankAccountDto);
        model.addAttribute("account", userAccount);
        log.info("Home page displayed");
        return ("home");
    }

}
