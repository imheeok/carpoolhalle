package com.carpoolhalle.group.validator;

import com.carpoolhalle.group.GroupRepository;
import com.carpoolhalle.group.form.GroupForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class GroupFormValidator implements Validator {

    private final GroupRepository groupRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return GroupForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GroupForm groupForm = (GroupForm)target;
        if(groupRepository.existsByPath(groupForm.getPath())){
            errors.rejectValue("path","wrong.path","Path is invalid value.");
        }
    }
}
