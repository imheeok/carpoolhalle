package com.carpoolhalle.group;

import com.carpoolhalle.account.CurrentUser;
import com.carpoolhalle.domain.CarpoolGroup;
import com.carpoolhalle.group.form.GroupForm;
import com.carpoolhalle.domain.Account;
import com.carpoolhalle.group.validator.GroupFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final ModelMapper modelMapper;
    private final GroupFormValidator groupFormValidator;

//    @InitBinder("groupForm")
//    public void groupFormInitBinder(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(groupFormValidator);
//    }

    @GetMapping("/newGroupForm")
    public String newGroupForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new GroupForm());
        return "group/form";

    }
    @PostMapping("/newGroupForm")
    public String newGroupSubmit(@CurrentUser Account account, @Valid GroupForm groupForm, Errors errors){
        if(errors.hasErrors()){
            return "group/form";
        }
        CarpoolGroup newGroup = groupService.createNewGroup(modelMapper.map(groupForm, CarpoolGroup.class), account);
        return "redirect:/form/" + URLEncoder.encode(newGroup.getPath(), StandardCharsets.UTF_8);
    }
}
