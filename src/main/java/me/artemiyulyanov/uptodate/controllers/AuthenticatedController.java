package me.artemiyulyanov.uptodate.controllers;

import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public abstract class AuthenticatedController {
    @Autowired
    private UserService userService;

    protected boolean isUserAuthorized() {
        return getAuthorizedUser().isPresent();
    }

    protected Optional<User> getAuthorizedUser() {
        try {
            return userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}