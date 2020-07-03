package com.jeferson.os.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "roles",
        schema = "public",
        indexes = {@Index(name = "roles_idx", columnList = "id,name")},
        uniqueConstraints = {@UniqueConstraint(name = "roles_unique", columnNames = {"id", "name"})}
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Override
    public String getAuthority() {
        return this.getName();
    }
}
