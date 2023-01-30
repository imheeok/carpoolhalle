package com.carpoolhalle.account;

import com.carpoolhalle.ConsoleMailSender;
import com.carpoolhalle.domain.Account;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailToken();
        sendSignupConfirmEmail(newAccount);
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword())) //bcrypt
                .carpoolCreatedByWeb(true)
                .carpoolEnrollmentResultByWeb(true)
                .carpoolUpdateByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    private void sendSignupConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("carpoolhalle, Verify sign-up");
        mailMessage.setText("/verify/email?token=" + newAccount.getEmailToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }


}
