package com.carpoolhalle.zone;

import com.carpoolhalle.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCountyAndCity(String countyName, String cityName);
}
