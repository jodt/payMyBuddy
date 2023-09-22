package com.openclassrooms.payMyBuddy.security;

import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findByMail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User.
                withUsername(user.get().getMail())
                .password(user.get().getPassword())
                .build();

        return userDetails;
    }

}
