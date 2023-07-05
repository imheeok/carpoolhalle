package com.carpoolhalle.group;

import com.carpoolhalle.domain.Account;
import com.carpoolhalle.domain.CarpoolGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository repository;

    public CarpoolGroup createNewGroup(CarpoolGroup group, Account account) {
        CarpoolGroup newGroup = repository.save(group);
        newGroup.addManager(account);
        return newGroup;
    }
}
