package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.PaymentService;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentController {

    private final UserService userService;

    private final PaymentService paymentService;

    private final UserMapper userMapper;

    public PaymentController(UserService userService, PaymentService paymentService, UserMapper userMapper) {
        this.userService = userService;
        this.paymentService = paymentService;
        this.userMapper = userMapper;
    }

    @GetMapping("/payment")
    public String getPayment(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "3") int size) {
        User user = this.userService.getLoggedUser();
        UserDTO userDTO = this.userMapper.asUserDTO(user);
        Page<PaymentDTO> paymentDTOS = this.paymentService.getAllPayments(userDTO, page, size);
        model.addAttribute("user", userDTO);
        model.addAttribute("payment", new PaymentDTO());
        model.addAttribute("payments", paymentDTOS.getContent());
        model.addAttribute("pages", new int[(paymentDTOS.getTotalPages())]);
        model.addAttribute("currentPage", page);
        return ("payment");
    }

    @PostMapping("/payment")
    public String makePayment(@Valid @ModelAttribute("payment") PaymentDTO paymentDTO, Errors errors, Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "3") int size) {

        User user = this.userService.getLoggedUser();
        UserDTO userDTO = this.userMapper.asUserDTO(user);


        if (errors.hasErrors()) {
            Page<PaymentDTO> paymentDTOS = this.paymentService.getAllPayments(userDTO, page, size);
            model.addAttribute("user", userDTO);
            model.addAttribute("payments", paymentDTOS.getContent());
            model.addAttribute("pages", new int[(paymentDTOS.getTotalPages())]);
            model.addAttribute("currentPage", page);
            return ("payment");
        }

        try {
            this.paymentService.makePayment(paymentDTO, userDTO);
        } catch (InsufficientBalanceException e) {
            return ("redirect:payment?balanceError");
        }
        System.out.println(page);
        return ("redirect:/payment?page=" + page + "&success");
    }

}
