package com.carpoolhalle.account;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor //[lombok] final 타입의 member variable 생성자로 만들어 준다.
public class SignUpFormValidator implements Validator {

    @NonNull
    private AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass){
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object object, Errors errors){
        SignUpForm signUpForm = (SignUpForm)object;
        if(accountRepository.existsByEmail(signUpForm.getEmail())){
             errors.rejectValue("email", "invalid.email",new Object[]{signUpForm.getEmail()}, "Account Already Exists.");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickname())){
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "Account Already Exists.");
        }
    }
}
