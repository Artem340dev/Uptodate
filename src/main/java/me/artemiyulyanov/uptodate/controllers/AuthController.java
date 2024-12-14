package me.artemiyulyanov.uptodate.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.artemiyulyanov.uptodate.jwt.JWTTokenService;
import me.artemiyulyanov.uptodate.jwt.JWTUtil;
import me.artemiyulyanov.uptodate.requests.LoginRequest;
import me.artemiyulyanov.uptodate.requests.RequestService;
import me.artemiyulyanov.uptodate.requests.ServerResponse;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JWTTokenService jwtTokenService;

    @Autowired
    private RequestService requestService;

    @PostMapping("/login")
    public ResponseEntity<ServerResponse> login(@RequestBody LoginRequest loginRequest, Model model) {
//        if (type.equals("email")) {
//            String email = loginRequest.getEmail();
//
//            if (email == null || !userService.userExistsByEmail(loginRequest.getEmail())) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(String.format(ERROR_TEMPLATE, "The user does not exist!"));
//            }
//
//            emailService.sendVerificationCodeTo(email);
//            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(String.format(ERROR_TEMPLATE, "The 6-digit verification code has been send to your email address!"));
//        }

//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(String.format(ERROR_TEMPLATE, "User has not been authorized: the type of authorization is invalid!"));

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (!userService.isUserVaild(username, password)) {
            return requestService.executeError(HttpStatus.UNAUTHORIZED, 10, "User is invalid!");
        }

        String token = jwtUtil.generateToken(username);
        return requestService.executeCustomTemplate(HttpStatus.OK, 200, Map.of("jwt_token", token));

    }

//    @PostMapping("/auth/login/verify-code")
//    public ResponseEntity<String> verifyCode(@RequestBody LoginRequest loginRequest, Model model) {
//        String email = loginRequest.getEmail();
//        String verificationCode = loginRequest.getVerificationCode();
//
//        if (emailService.validateCode(email, verificationCode)) {
//            Optional<User> user = userService.findByEmail(email);
//            emailService.authUser(email);
//
//            String token = jwtUtil.generateToken(user.get().getUsername());
//            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(String.format("{\"jwt_token\": \"%s\"}", token));
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(String.format(ERROR_TEMPLATE, "User has not been authorized: User is invalid!"));
//    }

    @PostMapping("/logout")
    public ResponseEntity<ServerResponse> logout(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request, HttpServletResponse response) {
        String token = authorizationHeader.substring(7);

        if (jwtTokenService.logout(token)) {
            return requestService.executeMessage(HttpStatus.OK, 200, "The logout has been performed successfully!");
        }

        return requestService.executeError(HttpStatus.BAD_REQUEST, 11, "User is not authorized!");
    }
}