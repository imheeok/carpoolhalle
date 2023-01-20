package com.carpoolhalle.account;

import com.carpoolhalle.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

//JpaRepository<id, type> 를 써서 AccountRepository 만듬.
@Transactional(readOnly = true) //성능의 이점을 가져오기 위해...
public interface AccountRepositoryCustom extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
