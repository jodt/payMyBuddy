package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BuddyMapper;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.service.BuddyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BuddiesController {

    private final BuddyService buddyService;

    private final BuddyMapper buddyMapper;

    public BuddiesController(BuddyService buddyService, BuddyMapper buddyMapper) {
        this.buddyService = buddyService;
        this.buddyMapper = buddyMapper;
    }

    @GetMapping("/buddies")
    public String showAddBuddyForm(Model model) {
        List<BuddyDTO> buddiesDTO = this.buddyService.getAllUsersAsBuddyDTO();
        model.addAttribute("users", buddiesDTO);
        model.addAttribute("buddyToAdd", new BuddyDTO());
        return ("buddies");
    }

    @PostMapping("/buddies")
    public String addBuddy(@Valid @ModelAttribute("buddyToAdd") BuddyDTO buddyDTO, Errors errors, Model model) {

        if (errors.hasErrors()) {
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
