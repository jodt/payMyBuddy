package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.AccountMapper;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Controller
public class HomeController {

    private final AccountService accountService;

    public HomeController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
    }

    @GetMapping("/home")
    public String showHomePage(Principal principal, Model model) {
        log.info("GET /Home called");
        AccountDTO userAccount = this.accountService.findAccountDtoByUserMail(principal.getName());
        model.addAttribute("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("account", userAccount);
        log.info("Home page displayed");
        return ("home");
    }

}
