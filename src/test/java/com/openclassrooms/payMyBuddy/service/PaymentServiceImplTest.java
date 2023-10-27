package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.PaymentMapper;
import com.openclassrooms.payMyBuddy.controller.mapper.PaymentMapperImpl;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    AccountService accountService;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PaymentMapper paymentMapper;

    @InjectMocks
    PaymentServiceImpl paymentService;


    private User user;

    private UserDTO userDTO;

    private Account issuerAccount;

    private Account receiverAccount;

    private Payment payment;

    private PaymentDTO paymentDTO;

    @Captor
    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);


    @BeforeEach
    void init() {


        user = User.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .build();

        userDTO = UserDTO.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .build();


        payment = Payment.builder()
                .amount(100)
                .description("remboursement")
                .issuerAccount(issuerAccount)
                .receiverAccount(receiverAccount)
                .build();


        paymentDTO = PaymentDTO.builder()
                .amount(new BigDecimal(100))
                .description("remboursement")
                .receiverMail("receiver@gmail.com")
                .build();

        issuerAccount = Account.builder()
                .number("123456789")
                .balance(500)
                .user(user)
                .id(1)
                .payments(List.of(payment))
                .build();

        receiverAccount = Account.builder()
                .number("987654321")
                .balance(200)
                .user(User.builder()
                        .mail("receiver@gmail.com")
                        .build())
                .id(2)
                .build();

    }

    @Test
    @DisplayName("Should make payment")
    void shouldMakePayment() throws InsufficientBalanceException {

        when(this.accountService.findByUserMail("john@test.com")).thenReturn(Optional.of(issuerAccount));
        when(this.accountService.findByUserMail("receiver@gmail.com")).thenReturn(Optional.of(receiverAccount));

        when(this.paymentMapper.asPayment(paymentDTO, issuerAccount, receiverAccount)).thenReturn(payment);
        when(this.paymentRepository.save(payment)).thenReturn(payment);

        when(accountService.saveAccount(issuerAccount)).thenReturn(issuerAccount);
        when(accountService.saveAccount(receiverAccount)).thenReturn(receiverAccount);

        Payment result = this.paymentService.makePayment(paymentDTO, userDTO);

        assertEquals((500 - 100 - (100 * 0.05)), issuerAccount.getBalance());
        assertEquals(200 + 100, receiverAccount.getBalance());
        assertNotNull(result);
        assertEquals(payment, result);

        verify(accountService).findByUserMail("john@test.com");
        verify(accountService).findByUserMail("receiver@gmail.com");
        verify(paymentMapper).asPayment(paymentDTO, issuerAccount, receiverAccount);
        verify(paymentRepository).save(payment);
        verify(accountService).saveAccount(issuerAccount);
        verify(accountService).saveAccount(receiverAccount);

    }

    @Test
    @DisplayName("Should not make payment -> InsufficientBalanceException")
    void shouldNotMakePaymentInsufficientBalanceException() throws InsufficientBalanceException {

        PaymentDTO paymentTooHigh = PaymentDTO.builder()
                .amount(new BigDecimal(1000))
                .description("remboursement")
                .receiverMail("receiver@gmail.com")
                .build();

        when(this.accountService.findByUserMail("john@test.com")).thenReturn(Optional.of(issuerAccount));
        when(this.accountService.findByUserMail("receiver@gmail.com")).thenReturn(Optional.of(receiverAccount));

        assertThrows(InsufficientBalanceException.class, () -> this.paymentService.makePayment(paymentTooHigh, userDTO));

        verify(accountService).findByUserMail("john@test.com");
        verify(accountService).findByUserMail("receiver@gmail.com");
        verify(accountService, never()).saveAccount(issuerAccount);
        verify(accountService, never()).saveAccount(receiverAccount);
        verify(paymentMapper, never()).asPayment(paymentTooHigh, issuerAccount, receiverAccount);
        verify(paymentRepository, never()).save(any());

    }


    @Test
    @DisplayName("Should get all payments")
    void shouldGetAllPayments() {

        when(this.accountService.findByUserMail("john@test.com")).thenReturn(Optional.of(issuerAccount));
        when(this.paymentMapper.asPaymentDTO(payment, payment.getReceiverAccount())).thenReturn(paymentDTO);

        Page<PaymentDTO> result = this.paymentService.getAllPayments(userDTO, 0, 3);

        assertNotNull(result);
        System.out.println(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(paymentDTO, result.getContent().get(0));

        verify(accountService).findByUserMail("john@test.com");
        verify(paymentMapper).asPaymentDTO(payment, payment.getReceiverAccount());

    }

}