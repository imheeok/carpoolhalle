package com.carpoolhalle.settings;

import com.carpoolhalle.account.AccountRepository;
import com.carpoolhalle.account.AccountService;
import com.carpoolhalle.account.SignUpForm;
import com.carpoolhalle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("imheeok");
        signUpForm.setEmail("your@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }
    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Profile Update form - input CORRECT")
    @Test
    void updateProfile() throws Exception{
        String bio = "add a bio.";
        mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("imheeok");
        assertEquals(bio, account.getBio());
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Profile Update form - input ERROR")
    @Test
    void updateProfile_error() throws Exception{
        String bio = "add a bio 12345678910 12345678910 12345678910";
        mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("imheeok");
        assertNull(account.getBio());
    }
}