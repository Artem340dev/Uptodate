package me.artemiyulyanov.uptodate.controllers.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.controllers.auth.requests.LoginRequest;
import me.artemiyulyanov.uptodate.controllers.auth.requests.RegisterRequest;
import me.artemiyulyanov.uptodate.controllers.auth.requests.VerifyCodeRequest;
import me.artemiyulyanov.uptodate.jwt.JWTTokenService;
import me.artemiyulyanov.uptodate.jwt.JWTUtil;
import me.artemiyulyanov.uptodate.mail.EmailVerificationCode;
import me.artemiyulyanov.uptodate.mail.MailService;
import me.artemiyulyanov.uptodate.models.Role;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.web.RequestService;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JWTTokenService jwtTokenService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ServerResponse> login(@RequestBody LoginRequest loginRequest, Model model) {
        if (isUserAuthorized()) {
            return requestService.executeError(HttpStatus.UNAUTHORIZED, 11, "User is already authorized!");
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (!userService.isUserVaild(username, password)) {
            return requestService.executeError(HttpStatus.UNAUTHORIZED, 10, "User is invalid!");
        }

        String token = jwtUtil.generateToken(username);
        return requestService.executeCustomTemplate(HttpStatus.OK, 200, Map.of("jwt_token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<ServerResponse> register(@RequestBody RegisterRequest registerRequest, Model model) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        if (userService.userExists(username, email)) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 10, "User already exists!");
        }

        registerRequest.setPassword(passwordEncoder.encode(password));
        EmailVerificationCode emailVerificationCode = mailService.sendCode(email, List.of(
                EmailVerificationCode.Credential
                        .builder()
                        .key("registerRequest")
                        .value(registerRequest)
                        .build()
        ));
        return requestService.executeEntity(HttpStatus.OK, 200, emailVerificationCode);
    }

    @PostMapping("/register/verify-code")
    public ResponseEntity<ServerResponse> registerVerifyCode(@RequestBody VerifyCodeRequest verifyCodeRequest, Model model) {
        String email = verifyCodeRequest.getEmail();
        String code = verifyCodeRequest.getCode();

        if (!mailService.validateCode(email, code)) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 10, "The code is invalid!");
        }

        EmailVerificationCode emailVerificationCode = mailService.getVerificationCode(email);
        RegisterRequest registerRequest = emailVerificationCode.getCredential("registerRequest").getValue(RegisterRequest.class);

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(email)
                .password(registerRequest.getPassword())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .build();

        mailService.enterCode(email, code);
        userService.createNewUser(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return requestService.executeCustomTemplate(HttpStatus.OK, 200, Map.of("jwt_token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<ServerResponse> logout(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request, HttpServletResponse response) {
        String token = authorizationHeader.substring(7);

        if (jwtTokenService.logout(token)) {
            return requestService.executeMessage(HttpStatus.OK, 200, "The logout has been performed successfully!");
        }

        return requestService.executeError(HttpStatus.BAD_REQUEST, 11, "User is not authorized!");
    }
}