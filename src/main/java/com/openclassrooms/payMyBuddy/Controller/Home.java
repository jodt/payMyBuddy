package com.openclassrooms.payMyBuddy.Controller;

import com.openclassrooms.payMyBuddy.Controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.Controller.mapper.AccountMapper;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.service.AccountService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class Home {

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    public Home(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Account> account = this.accountService.findByUserMail(userMail);
        AccountDTO userAccount = null;
        if (account.isPresent()) {
            userAccount = this.accountMapper.asAccountDTO(account.get());
        }
        model.addAttribute("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("account", userAccount);
        return ("home");
    }

}
