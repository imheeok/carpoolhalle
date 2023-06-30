package com.carpoolhalle.account;

import com.carpoolhalle.config.AppProperties;
import com.carpoolhalle.domain.Account;
import com.carpoolhalle.domain.Tag;
import com.carpoolhalle.domain.Zone;
import com.carpoolhalle.mail.EmailMessage;
import com.carpoolhalle.mail.EmailService;
import com.carpoolhalle.settings.form.Notifications;
import com.carpoolhalle.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    //private final JavaMailSender javaMailSender;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;


    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendSignupVerificationEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailToken();
        return accountRepository.save(account);
    }

    public void sendSignupVerificationEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("link","/checkEmailToken?token="+newAccount.getEmailToken()+
                            "&email="+newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName","Verify your email.");
        context.setVariable("message","Click the link below.");
        context.setVariable("host",appProperties.getHost());

        String message = templateEngine.process("mail/simple-link",context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("Carpool Halle, Verify your email")
                .message(message)
                .build();
        emailService.send(emailMessage);
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

        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account); //merge
    }

    public void updateNotifications(Account account, Notifications notifications) {
        /*account.setCarpoolUpdatedByEmail(notifications.isCarpoolUpdatedByEmail());
        account.setCarpoolUpdatedByWeb(notifications.isCarpoolUpdatedByWeb());*/
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        signin(account);
    }

    public void sendSigninLink(Account account) {
        Context context = new Context();
        context.setVariable("link","/checkEmailToken?token="+account.getEmailToken()+
                "&email="+account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName","Verify your email.");
        context.setVariable("message","Click the link below.");
        context.setVariable("host",appProperties.getHost());

        String message = templateEngine.process("mail/simple-link",context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("Carpool Halle, Verify your email")
                .message(message)
                .build();
        emailService.send(emailMessage);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public Set<Zone> addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(zone));
    }
}
