package com.carpoolhalle.account;

import com.carpoolhalle.ConsoleMailSender;
import com.carpoolhalle.domain.Account;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final ConsoleMailSender consoleMailSender;

    // form 객체 이름은 변수명이 아니라 type의 camel case를 따라간다..
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/signup")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        if(errors.hasErrors()){
            return "account/signup";
        }

        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword()) // TODO encoding
                .carpoolCreatedByWeb(true)
                .carpoolEnrollmentResultByWeb(true)
                .carpoolUpdateByWeb(true)
                .build();

        Account newAccount = accountRepository.save(account);

        newAccount.generateEmailVerifierToken();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("carpoolhalle, Verify sign-up");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailVerifierToken() +
                "&email=" + newAccount.getEmail());

        consoleMailSender.send(mailMessage);
        return "redirect:/";
    }
}
