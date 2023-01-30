package com.carpoolhalle.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of ="id")
@Builder @AllArgsConstructor @NoArgsConstructor

public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location; // varchar(255)

    //프로필 이미지의 경우는 더 큰 사이즈가 필요할 수도 있기에 text 타입으로 매핑.
    @Lob @Basic(fetch=FetchType.EAGER)
    private String profileImage;

    private boolean carpoolCreatedByEmail;

    private boolean carpoolCreatedByWeb;

    private boolean carpoolEnrollmentResultByEmail;

    private boolean carpoolEnrollmentResultByWeb;

    private boolean carpoolUpdateByEmail;

    private boolean carpoolUpdateByWeb;

    public void generateEmailToken() {
        this.emailToken = UUID.randomUUID().toString();
    }
}
