package com.berriesoft.springsecurity.user;


import com.berriesoft.springsecurity.token.Token;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
@SQLDelete(sql = "UPDATE _user SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted=0")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String firstname;

    @NotNull
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String lastname;

    @NotNull
    @Size(min = 5)
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", message = "Please enter a valid email")
    private String email;

    @NotNull
    @Size(min = 8)
    @ToString.Exclude
    private String password;


    @NotNull
    private int isDeleted;

    @NotNull
    private int isInactive;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

}
