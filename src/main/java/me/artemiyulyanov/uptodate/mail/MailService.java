package me.artemiyulyanov.uptodate.mail;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MailService {
    public static final Duration VERIFICATION_CODE_EXPIRATION = Duration.ofMinutes(15);

    @Autowired
    private RedisTemplate<String, EmailVerificationCode> verificationCodes;

    @Autowired
    private JavaMailSender mailSender;

    public EmailVerificationCode sendCode(String email, List<EmailVerificationCode.Credential> credentials) {
        if (isCodeSent(email)) {
            verificationCodes.delete(email);
        }

        EmailVerificationCode verificationCode = EmailVerificationCode.builder()
                .email(email)
                .code(Integer.toString(generateRandomCode()))
                .credentials(credentials)
                .build();
        verificationCodes.opsForValue().set(email, verificationCode, VERIFICATION_CODE_EXPIRATION);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification code");
        message.setText(String.format("Hi! Your verification code is: %s. Enter it to get authenticated", verificationCode.getCode()));
        mailSender.send(message);

        return verificationCode;
    }

    public EmailVerificationCode getVerificationCode(String email) {
        return verificationCodes.opsForValue().get(email);
    }

    public boolean enterCode(String email, String code) {
        if (validateCode(email, code)) {
            verificationCodes.delete(email);
            return true;
        }

        return false;
    }

    public boolean validateCode(String email, String code) {
        return isCodeSent(email) && Objects.requireNonNull(verificationCodes.opsForValue().get(email)).getCode().equals(code);
    }

    public boolean isCodeSent(String email) {
        return Boolean.TRUE.equals(verificationCodes.hasKey(email));
    }

    private int generateRandomCode() {
        return (int) (Math.random() * 899999) + 100000;
    }
}