package com.carpoolhalle.settings;

import com.carpoolhalle.account.AccountRepository;
import com.carpoolhalle.account.AccountService;
import com.carpoolhalle.account.SignUpForm;
import com.carpoolhalle.domain.Account;
import com.carpoolhalle.domain.Tag;
import com.carpoolhalle.settings.form.PasswordForm;
import com.carpoolhalle.settings.form.TagForm;
import com.carpoolhalle.tag.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.engine.Setting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static com.carpoolhalle.settings.SettingController.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired PasswordEncoder passwordEncoder;


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

    // PROFILE ========================================================================
    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[PROFILE] Profile Update form")
    @Test
    void updateProfileForm() throws Exception{
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[PROFILE] Update Profile - input CORRECT")
    @Test
    void updateProfile() throws Exception{
        String bio = "add a bio.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                    .param("bio", bio)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("imheeok");
        assertEquals(bio, account.getBio());
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[PROFILE] Update Profile - input ERROR")
    @Test
    void updateProfile_error() throws Exception{
        String bio = "add a bio 12345678910 12345678910 12345678910";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS + PROFILE))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("imheeok");
        assertNull(account.getBio());
    }

    // PASSWORD ========================================================================
    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[PASSWORD] Update Password form")
    @Test
    void updatePasswordForm() throws Exception{
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[PASSWORD] Update Password - input Confirm CORRECT")
    @Test
    void updatePassword() throws Exception{
        String newPassword = "newPassword11";
        String newPasswordConfirm = "newPassword11";
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm",newPasswordConfirm)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("imheeok");
        assertTrue(passwordEncoder.matches(newPassword, account.getPassword()));
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[PASSWORD] Update Password - input Confirm Error")
    @Test
    void updatePassword_error() throws Exception{
        String newPassword = "newPassword11";
        String newPasswordConfirm = "newPassword22";
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm",newPasswordConfirm)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("imheeok");
        assertFalse(passwordEncoder.matches(newPassword, account.getPassword()));
    }

    // ACCOUNT ========================================================================
    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[ACCOUNT] Nickname Update form")
    @Test
    void updateAccountForm() throws Exception{
        mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[ACCOUNT] Update Account - input CORRECT")
    @Test
    void updateAccount() throws Exception{
        String newNickname = "newnickname";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + ACCOUNT))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("newnickname");
        assertEquals(newNickname, account.getNickname());
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[ACCOUNT] Update Account - input ERROR")
    @Test
    void updateAccount_error() throws Exception{
        String newNickname = "newNickname";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name( SETTINGS + ACCOUNT))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(model().hasErrors());
    }

    // TAG ============================================================================
    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[TAG] Tag Update form")
    @Test
    void updateTagsForm() throws Exception{
        mockMvc.perform(get( ROOT + SETTINGS + TAGS))
                .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[TAG] Add Tag")
    @Test
    void addTag() throws Exception{
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post( ROOT + SETTINGS + TAGS + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account account = accountRepository.findByNickname("imheeok");
        assertTrue(account.getTags().contains(newTag));
    }

    @WithUserDetails(value = "imheeok", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[TAG] Remove Tag")
    @Test
    void removeTag() throws Exception{
        Account account = accountRepository.findByNickname("imheeok");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(account, newTag);

        assertTrue(account.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post( ROOT + SETTINGS + TAGS+"/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(account.getTags().contains("newTag"));
    }
}