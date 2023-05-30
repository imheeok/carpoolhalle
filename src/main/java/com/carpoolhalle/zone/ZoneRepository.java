package com.carpoolhalle.zone;

import com.carpoolhalle.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCountyAndCity(String countyName, String cityName);
}
