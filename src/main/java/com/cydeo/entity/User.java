package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@Where(clause = "is_deleted=false")
public class User extends BaseEntity {

    @Column(unique = true)
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String phone;
    private boolean enabled;
    @ManyToOne
    private Role role;
    @ManyToOne
    private Company company;

    public User(Long id, LocalDateTime insertDateTime, Long insertUserId, LocalDateTime lastUpdateDateTime, Long lastUpdateUserId, Boolean isDeleted, String username, String password, String firstname, String lastname, String phone, boolean enabled, Role role, Company company) {
        super(id, insertDateTime, insertUserId, lastUpdateDateTime, lastUpdateUserId, isDeleted);
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.enabled = enabled;
        this.role = role;
        this.company = company;
    }
}
