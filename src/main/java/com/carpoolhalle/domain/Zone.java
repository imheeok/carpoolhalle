package com.carpoolhalle.domain;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @EqualsAndHashCode(of ="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Zone {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String county;

    @Column(unique = true, nullable = false)
    private String city;

    @Override
    public String toString() {
        return String.format("%s / %s", county, city);
    }
}
