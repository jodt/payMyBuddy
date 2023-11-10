package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BuddyMapper;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.service.BuddyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
public class BuddiesController {

    private final BuddyService buddyService;

    private final BuddyMapper buddyMapper;

    public BuddiesController(BuddyService buddyService, BuddyMapper buddyMapper) {
        this.buddyService = buddyService;
        this.buddyMapper = buddyMapper;
    }

    @GetMapping("/buddies")
    public String showAddBuddyForm(Model model) throws ResourceNotFoundException {
        log.info("GET /buddies called");
        List<BuddyDTO> buddiesDTO = this.buddyService.getAllUsersAsBuddyDTO();
        model.addAttribute("users", buddiesDTO);
        model.addAttribute("buddyToAdd", new BuddyDTO());
        log.info("Buddies page displayed");
        return ("buddies");
    }

    @PostMapping("/buddies")
    public String addBuddy(@Valid @ModelAttribute("buddyToAdd") BuddyDTO buddyDTO, Errors errors, Model model) throws ResourceNotFoundException {
        log.info("POST /buddies called -> start of the process to add a buddy");
        if (errors.hasErrors()) {
            log.info("Errors in form validation on field {}", errors.getFieldError().getField());
            List<BuddyDTO> buddiesDTO = this.buddyService.getAllUsersAsBuddyDTO();
            model.addAttribute("users", buddiesDTO);
            return ("buddies");
        }
        try {
            this.buddyService.addBuddy(buddyDTO.getBuddyEmail());
        } catch (AlreadyBuddyExistException e) {
            return ("redirect:buddies?addBuddyFailed");
        }

        return ("redirect:payment?buddyAdded");
    }

}
