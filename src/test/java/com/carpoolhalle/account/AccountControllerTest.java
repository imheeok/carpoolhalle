package com.carpoolhalle.account;

import com.carpoolhalle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("Test for signup view")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/signup"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("Signup - input ERROR")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception{

        mockMvc.perform(post("/signup")
                        .param("nickname","heeok")
                        .param("email", "email...")
                        .param("password","12345")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/signup"));

    }
    @DisplayName("Signup - input SUCCESS")
    @Test
    void signUpSubmit_with_correct_input() throws Exception{

        mockMvc.perform(post("/signup")
                        .param("nickname","heeok")
                        .param("email", "xxheeok@gmail.com")
                        .param("password","12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertTrue(accountRepository.existsByEmail("xxheeok@gmail.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}