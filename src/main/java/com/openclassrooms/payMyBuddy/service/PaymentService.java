package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.Controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.Controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    public Payment makePayment(PaymentDTO paymentDTO, UserDTO userDTO);

    public Page<PaymentDTO> getAllPayments(UserDTO issuerUser, int page, int size);

}
