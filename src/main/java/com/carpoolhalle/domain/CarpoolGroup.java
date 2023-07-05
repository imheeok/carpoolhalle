package com.carpoolhalle.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor @NoArgsConstructor
public class CarpoolGroup {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic
    private String description;

    @Lob @Basic
    private String image;

    @ManyToMany
    private Set<Tag> tags;

    @ManyToMany
    private Set<Zone> zones;

    public void addManager(Account account) {
        this.managers.add(account);
    }
}
