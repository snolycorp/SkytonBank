package org.example.controller;

import org.example.model.User;
import org.example.service.EmailService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already taken");
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                User dbUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();

                if (dbUser.isTwoFactorEnabled()) {
                    String token = String.valueOf(new Random().nextInt(999999));
                    dbUser.setTwoFactorToken(token);
                    userService.saveUser(dbUser);
                    emailService.sendVerificationEmail(dbUser.getEmail(), token);
                    model.addAttribute("user", dbUser);
                    return "verify";
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid username or password");
                return "login";
            }
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/verify")
    public String showVerificationForm(Model model, @RequestParam String username) {
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "login";
        }
        model.addAttribute("user", user);
        return "verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String token, @RequestParam String username, Model model) {
        User dbUser = userService.findByUsername(username).orElseThrow();

        if (dbUser.getTwoFactorToken().equals(token)) {
            dbUser.setTwoFactorVerified(true);
            userService.saveUser(dbUser);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid verification token");
            model.addAttribute("user", dbUser);
            return "verify";
        }
    }
}
