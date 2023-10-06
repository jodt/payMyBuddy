package com.openclassrooms.payMyBuddy.Controller;

import com.openclassrooms.payMyBuddy.Controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.Controller.mapper.AccountMapper;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class HomeController {

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    public HomeController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @GetMapping("/home")
    public String showHomePage(Principal principal, Model model) {
        Optional<Account> account = this.accountService.findByUserMail(principal.getName());
        AccountDTO userAccount = null;
        if (account.isPresent()) {
            userAccount = this.accountMapper.asAccountDTO(account.get());
        }
        model.addAttribute("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("account", userAccount);
        return ("home");
    }

}
