package ru.CloudStorage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.models.User;
import ru.CloudStorage.repository.UserRepository;

import java.util.Collections;


@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        return new CustomUser(user.getId(), user.getUsername(), user.getPassword(),  Collections.emptyList());

    }

     public void registerUser(String username, String rawPassword){
         log.warn("Registering user: {}", username); // Логируем имя пользователя
         String encodedPassword = passwordEncoder.encode(rawPassword);

         User user = new User();
         user.setUsername(username);
         user.setPassword(encodedPassword);
         userRepository.saveAndFlush(user);
         log.warn("User registered successfully: {}", username); // Логируем успешную регистрацию
    }

}