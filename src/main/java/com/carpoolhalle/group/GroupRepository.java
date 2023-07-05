package com.carpoolhalle.group;

import com.carpoolhalle.domain.CarpoolGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface GroupRepository extends JpaRepository<CarpoolGroup, Long>{

    boolean existsByPath(String path);
}
