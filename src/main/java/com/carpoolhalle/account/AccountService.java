package com.carpoolhalle.account;

import com.carpoolhalle.domain.Account;
import com.carpoolhalle.settings.Notifications;
import com.carpoolhalle.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;


    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailToken();
        sendSignupVerificationEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword())) //bcrypt
                .carpoolCreatedByWeb(true)
                .carpoolEnrollmentResultByWeb(true)
                .carpoolUpdatedByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    public void sendSignupVerificationEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("carpoolhalle, Verify your email");
        mailMessage.setText("/checkEmailToken?token=" + newAccount.getEmailToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }


    public void signin(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))); //get list of authority

        SecurityContextHolder.getContext().setAuthentication(token);

    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if(account == null){
            account = accountRepository.findByNickname(username);
        }
        if(account == null){
            throw new UsernameNotFoundException(username);
        }
        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        signin(account);
    }

    public void updateProfile(Account account, Profile profile) {
        account.setUrl(profile.getUrl());
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        account.setBio(profile.getBio());
        account.setProfileImage(profile.getProfileImage());

        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account); //merge
    }

    public void updateNotifications(Account account, Notifications notifications) {
        account.setCarpoolUpdatedByEmail(notifications.isCarpoolUpdatedByEmail());
        account.setCarpoolUpdatedByWeb(notifications.isCarpoolUpdatedByWeb());
        accountRepository.save(account);
    }
}
