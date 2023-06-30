package com.carpoolhalle.domain;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of ="id")
@Builder @AllArgsConstructor @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"county", "city"}))
public class Zone {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String county;

    @Column(nullable = true)
    private String city;

    @Override
    public String toString() {
        return String.format("%s / %s", county, city);
    }
}
