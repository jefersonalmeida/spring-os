package com.jeferson.os.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeferson.os.core.util.Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "users",
        schema = "public",
        indexes = {@Index(name = "users_idx", columnList = "id,email,phone")},
        uniqueConstraints = {@UniqueConstraint(name = "users_unique", columnNames = {"id", "email"})}
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private Boolean active;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, length = 200)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @Size(max = 200)
    @Column(length = 200)
    private String password;

    @Column
    private String img;

    @Column(name = "activation_token")
    @JsonProperty(value = "activation_token")
    private String activationToken;

    @Column(name = "email_verified_at")
    @JsonProperty(value = "email_verified_at")
    private OffsetDateTime emailVerifiedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonProperty(value = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonProperty(value = "updated_at")
    private OffsetDateTime updatedAt;

    public User(String name, String email, String password, List<Role> roles) {
        super();
        this.setActive(false);
        this.setName(name);
        this.setEmail(email);
        this.setPassword(password);
        this.setRoles(roles);
        this.generateActivationToken();
    }

    public User(User user) {
        super();
        this.setId(user.getId());
        this.setActive(user.getActive());
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setPhone(user.getPhone());
        this.setPassword(user.getPassword());
        this.setImg(user.getImg());
        this.setActivationToken(user.getActivationToken());
        this.setEmailVerifiedAt(user.getEmailVerifiedAt());
        this.setRoles(user.getRoles());
        this.setCreatedAt(user.getCreatedAt());
        this.setUpdatedAt(user.getUpdatedAt());
    }

    public void generateActivationToken() {
        this.activationToken = Util.randomString();
    }

    @Override
    public String getUsername() {
        return email;
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
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.isEmpty() ? this.password : Util.encoder(password);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }
}
