package com.mini2.user_service.domain;

import com.mini2.user_service.secret.hash.SecureHashUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Slf4j
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "site_user")
@Getter
public class SiteUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name="email" , nullable = false, length = 50, unique = true)
    private String email;

    @Setter
    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    public static SiteUser create(String email, String password, String name) {
        SiteUser user = new SiteUser();
        user.setEmail(email);
        user.setPassword(SecureHashUtils.hash(password));
        user.setName(name);
        user.setRole(Role.USER);
        return user;
    }

}