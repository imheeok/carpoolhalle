package com.carpoolhalle.account;

import com.carpoolhalle.domain.Account;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

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
        Account account = accountService.processNewAccount(signUpForm);
        accountService.signin(account);
        return "redirect:/";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@CurrentUser Account account, Model model){
        model.addAttribute("email", account.getEmail());
        model.addAttribute("nickname", account.getNickname());
        return "account/verifyEmail";
    }

    @GetMapping("/resendVerificationEmail")
    public String resendVerificationEmail(@CurrentUser Account account, Model model){

        accountService.sendSignupVerificationEmail(account);
        return "redirect:/";
    }

    @GetMapping("/checkEmailToken")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/verifiedEmail";
        if(account == null){
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }
        accountService.completeSignUp(account);
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account){
        Account byNickname = accountRepository.findByNickname(nickname);
        if(nickname == null){
            throw new IllegalArgumentException("There is no account : " +nickname);
        }

        model.addAttribute(byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));
        return "account/profile";
    }

}
