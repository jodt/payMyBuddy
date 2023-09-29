package com.openclassrooms.payMyBuddy.Controller;

import com.openclassrooms.payMyBuddy.Controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.Controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.Controller.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.service.PaymentService;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        UserDTO userDTO = this.userService.getLoggedUser();
        Page<PaymentDTO> paymentDTOS = this.paymentService.getAllPayments(userDTO, page, size);
        model.addAttribute("user", userDTO);
        model.addAttribute("payment", new PaymentDTO());
        model.addAttribute("payments", paymentDTOS.getContent());
        model.addAttribute("pages", new int[(paymentDTOS.getTotalPages())]);
        return ("payment");
    }

    @PostMapping("/payment")
    public String makePayment(@Valid @ModelAttribute("payment") PaymentDTO paymentDTO, Errors errors, Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "3") int size) throws Exception {

        UserDTO userDTO = this.userService.getLoggedUser();

        model.addAttribute("user", userDTO);

        if (errors.hasErrors()) {
            return ("payment");
        }
        this.paymentService.makePayment(paymentDTO, userDTO);
        Page<PaymentDTO> paymentDTOS = this.paymentService.getAllPayments(userDTO, page, size);
        model.addAttribute("payment", new PaymentDTO());
        model.addAttribute("payments", paymentDTOS.getContent());
        return ("redirect:payment?success");
    }

    @GetMapping("/payment?success")
    public String getSuccessPaiement(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "3") int size) {
        UserDTO userDTO = this.userService.getLoggedUser();
        Page<PaymentDTO> paymentDTOS = this.paymentService.getAllPayments(userDTO, page, size);

        model.addAttribute("user", userDTO);
        model.addAttribute("payment", new PaymentDTO());
        model.addAttribute("payments", paymentDTOS.getContent());

        return ("payment");
    }


}
